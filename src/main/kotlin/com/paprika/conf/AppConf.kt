package com.paprika.conf

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

data class DatabaseConfiguration (
    val connectionUrl: String,
    val driver: String,
    val user: String,
    val password: String
)
object AppConf {
    private val applicationConfig: ApplicationConfig =
        HoconApplicationConfig(ConfigFactory.load().getConfig("application"))
    private val databaseConfig: ApplicationConfig = applicationConfig.config("database")
    private val filesConfig: ApplicationConfig = applicationConfig.config("files")
    private val botConfig: ApplicationConfig = applicationConfig.config("bot")
    private val jwtConfig: ApplicationConfig = applicationConfig.config("secure")

    val databaseConfiguration: DatabaseConfiguration =
        DatabaseConfiguration(
            databaseConfig.property("url").getString(),
            databaseConfig.property("driver").getString(),
            databaseConfig.property("user").getString(),
            databaseConfig.property("password").getString(),
        )

    val filePath: String = filesConfig.property("savePath").getString()

    val botToken: String = botConfig.property("token").getString()
    val botWebappUrl: String = botConfig.property("webappUrl").getString()
    val jwt: JwtConf = JwtConf(
        secret = jwtConfig.property("secret").getString(),
        domain = jwtConfig.property("domain").getString(),
        expiration = jwtConfig.property("expiration").getString().toLong()
    )
}