package com.paprika.plugins

import com.paprika.exceptions.ClientException
import com.paprika.exceptions.ServerError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun logError(exception: Exception, path: String) {
    println("There was a Client Exception: $exception, during the call: $path")
    println("Stack: ${exception.stackTraceToString()}")
}

/*

    Status pages plugin configuration. Here we catch all exception if they bubbles up and bases of what happened we explain to user

 */

fun Application.responseFilter() {
    install(StatusPages) {
        exception<ClientException> { call, cause ->
            logError(cause, call.request.path())
            call.respond(HttpStatusCode(cause.status, cause.statusDescription), cause)
        }
        exception<Exception> { call, cause ->
            logError(cause, call.request.path())
            call.respond(HttpStatusCode(500, "Internal server error"), ServerError("Internal server error"))
        }
        exception<ExposedSQLException> { call, cause ->
            logError(cause, call.request.path())
            call.respond(HttpStatusCode(500, "Internal server error"), ServerError(cause.message ?: "Internal server error"))
        }
    }
}