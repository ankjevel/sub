package sub

import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import org.json.JSONException
import org.json.JSONObject
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub

class RedisListener {
    fun run() {
        Thread(Runnable {
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
        }).start()
    }

    private fun handleMessage(ip: String) {
        println("Message received: \"$ip\"")

        val uuid = "${UUID.randomUUID()}"

        val keys = hashMapOf(
            "process" to "processing:$ip",
            "usr" to "usr:$ip"
        )

        val usrMeta = query.hmget(keys["usrMeta"],
            "ip",
            "uuid",
            "org",
            "city",
            "region",
            "base",
            "allow",
            "deny",
            "status"
        )

        println(usrMeta)

        if ((query.get(keys["process"]) != null) or (!query.hmget(keys["usr"], "uuid").isEmpty())) {
            return
        }

        query.set(keys["process"], uuid)
        query.expire(keys["process"], 10) // 120

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

        query.hmset(keys["usr"], meta)
        query.expire(keys["usr"], 10) // 86400

        // println(meta)
        println(json)
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
