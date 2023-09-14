package com.paprika.plugins

import io.ktor.server.application.*
import io.ktor.server.engine.*

fun Application.configureAdministration() {
    install(ShutDownUrl.ApplicationCallPlugin) {
        shutDownUrl = "/system/shutdown"
        exitCodeSupplier = { 0 }
    }
}
