package sub

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties as Props
import java.io.File
import java.util.*

private val config =
    Props.fromOptionalFile(
        File("src/main/resources/overrides.properties")
    ) overriding EnvironmentVariables() overriding Props.fromResource(
        "defaults.properties"
    ) overriding Props(
        location = Location("inline"),
        properties = Properties().apply {
            setProperty("env", "dev")
        }
    )

private object Defaults {
    val env = Key("env", stringType)
    val redisHost = Key("redis.host", stringType)
    val redisPort = Key("redis.port", intType)
    val redisChannel = Key("redis.channel", stringType)
    val ipInfoToken = Key("ipinfo.token", stringType)
    val location = Key("location", stringType)
}

object Config {
    object Redis {
        val host = config[Defaults.redisHost]
        val port = config[Defaults.redisPort]
        val channel = config[Defaults.redisChannel]
    }

    object IPInfo {
        val token = config[Defaults.ipInfoToken]
    }

    val env = config[Defaults.env]

    val location = config[Defaults.location]
}
