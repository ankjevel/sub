package sub

import com.natpryce.konfig.*
import java.io.File
import java.util.*
import com.natpryce.konfig.ConfigurationProperties as Props

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

private object Mappings {
    val env = Key("env", stringType)
    val redisHost = Key("redis.host", stringType)
    val redisPort = Key("redis.port", intType)
    val redisChannel = Key("redis.channel", stringType)
    val redisTTLProcess = Key("redis.ttl.process", intType)
    val redisTTLUsr = Key("redis.ttl.usr", intType)
    val redisTTLAllow = Key("redis.ttl.allow", intType)
    val redisKeysProcess = Key("redis.keys.process", stringType)
    val redisKeysUsr = Key("redis.keys.usr", stringType)
    val redisKeysAllow = Key("redis.keys.allow", stringType)
    val firebaseTopic = Key("firebase.topic", stringType)
    val ipInfoToken = Key("ipinfo.token", stringType)
    val location = Key("location", stringType)
}

object Config {
    object Redis {
        val host = config[Mappings.redisHost]
        val port = config[Mappings.redisPort]
        val channel = config[Mappings.redisChannel]

        object TTL {
            val process = config[Mappings.redisTTLProcess]
            val usr = config[Mappings.redisTTLUsr]
            val allow = config[Mappings.redisTTLAllow]
        }

        object Keys {
            val process = config[Mappings.redisKeysProcess]
            val usr = config[Mappings.redisKeysUsr]
            val allow = config[Mappings.redisKeysAllow]
        }
    }

    object IPInfo {
        val token = config[Mappings.ipInfoToken]
    }

    object Firebase {
        val topic = config[Mappings.firebaseTopic]
    }

    val env = config[Mappings.env]

    val location = config[Mappings.location]
}
