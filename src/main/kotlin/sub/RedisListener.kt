package sub

import org.json.JSONException
import org.json.JSONObject
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class RedisListener {
    init {
        thread(name = "RedisListener", start = true) {
            sub.subscribe(
                object : JedisPubSub() {
                    override fun onMessage(channel: String, message: String) {
                        if (!IP_REGEX.matches(message)) {
                            return
                        }

                        handleMessage(message)
                    }
                },
                Config.Redis.channel
            )
            sub.quit()
        }
    }

    private fun handleMessage(ip: String) {
        println("Message received: \"$ip\"")

        val uuid = "${UUID.randomUUID()}"
        val keyProcess = "${Config.Redis.Keys.process}:$ip"
        val keyUsr = "${Config.Redis.Keys.usr}:$ip"

        if ((query.get(keyProcess) != null) or (!query.hmget(keyUsr, "uuid").none { it != null })) {
            return
        }

        query.set(keyProcess, uuid)
        query.expire(keyProcess, Config.Redis.TTL.process)

        val res: String
        try {
            res = URL(
                "https://ipinfo.io/$ip?token=${Config.IPInfo.token}"
            ).run {
                openConnection().run {
                    this as HttpURLConnection
                    inputStream.bufferedReader().readText()
                }
            }
        } catch (e: Exception) {
            println("error: $e")
            return
        }

        val json: JSONObject
        try {
            json = JSONObject(res)
        } catch (e: JSONException) {
            println(e)
            return
        }

        val meta = hashMapOf(
            "ip" to ip,
            "org" to if (json.has("org")) json.getString("org") else "",
            "city" to if (json.has("city")) json.getString("city") else "",
            "region" to if (json.has("region")) json.getString("region") else "",
            "base" to "${Config.location}/$uuid",
            "allow" to "1",
            "deny" to "0",
            "status" to "",
            "uuid" to uuid
        )

        query.hmset(keyUsr, meta)
        query.expire(keyUsr, Config.Redis.TTL.usr)

        PushNotification.sendNotification(meta)
    }

    companion object {
        private const val IP_RANGE = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"

        val IP_REGEX = Regex(
            "\\b$IP_RANGE\\.$IP_RANGE\\.$IP_RANGE\\.$IP_RANGE\\b"
        )

        val sub: Jedis by lazy {
            Jedis(Config.Redis.host, Config.Redis.port)
        }

        val query: Jedis by lazy {
            Jedis(Config.Redis.host, Config.Redis.port)
        }
    }
}