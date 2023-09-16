package com.paprika.plugins

import com.paprika.exceptions.ClientException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.responseFilter() {
    install(StatusPages) {
        exception<ClientException> { call, cause ->
            println("There was a Client Exception: $cause, during the call: ${call.request.path()}")
            call.respond(HttpStatusCode(cause.status, cause.statusDescription), cause)
        }
        exception<Exception> { call, cause ->
            println("There was an exception: $cause, during the call: ${call.request.path()}")
            call.respond(HttpStatusCode(500, "Internal server error"), "Internal server error")
        }
    }
}