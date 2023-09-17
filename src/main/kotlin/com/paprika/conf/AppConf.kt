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
        HoconApplicationConfig(ConfigFactory.load().getConfig("application"));
    private val databaseConfig: ApplicationConfig = applicationConfig.config("database");
    private val filesConfig: ApplicationConfig = applicationConfig.config("files");

    val databaseConfiguration: DatabaseConfiguration =
        DatabaseConfiguration(
            databaseConfig.property("url").getString(),
            databaseConfig.property("driver").getString(),
            databaseConfig.property("user").getString(),
            databaseConfig.property("password").getString(),
        )
    val filePath: String = filesConfig.property("savePath").getString()
}