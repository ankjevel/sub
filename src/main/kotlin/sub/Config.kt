package sub

import com.natpryce.konfig.*
import java.io.File
import java.util.Properties

private val config =
    ConfigurationProperties.fromOptionalFile(
        File("src/main/resources/overrides.properties")
    ) overriding
    EnvironmentVariables() overriding
    ConfigurationProperties.fromResource(
        "defaults.properties"
    ) overriding
    ConfigurationProperties(
        location = Location("inline"),
        properties = Properties().apply {
            setProperty("env", "dev")
        }
    )

private object defaults {
    val env = Key("env", stringType)
    val redisHost = Key("redis.host", stringType)
    val redisPort = Key("redis.port", intType)
}

object Config {
    object Redis {
        val host = config[defaults.redisHost]
        val port = config[defaults.redisPort]
    }

    val env = config[defaults.env]
}
