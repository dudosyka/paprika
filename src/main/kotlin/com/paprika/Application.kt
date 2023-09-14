package com.paprika

import com.paprika.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(): Unit = EngineMain.main(arrayOf())
//{
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
//}

fun Application.module() {
//    configureMonitoring()
    configureSerialization()
    configureAdministration()
    configureRouting()
}
