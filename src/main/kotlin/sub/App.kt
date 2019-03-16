package sub

import sub.Config
import redis.clients.jedis.Jedis

class App {
    val jedis: Jedis

    init {
        jedis = Jedis(Config.Redis.host, Config.Redis.port)
    }

    val greeting: String
        get() {
            jedis.mset("foo", "bar")

            val result = jedis.mget("foo")

            println(result)

            return "Hello world."
        }
}

fun main(args: Array<String>) {
    println(App().greeting)
}
