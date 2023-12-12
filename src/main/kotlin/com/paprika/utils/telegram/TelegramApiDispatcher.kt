package com.paprika.utils.telegram

import com.paprika.conf.AppConf
import com.paprika.utils.telegram.dto.ApiCall
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


class TelegramApiDispatcher {
    val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            header("Content-Type", "application/json")
        }
    }

    suspend inline fun <reified T> call(request: ApiCall): T? {
        val sendUrl = "https://api.telegram.org/bot${AppConf.botToken}/${request.name}"
        return try {
            val res = client.post(sendUrl) {
                contentType(ContentType.Application.Json)
                setBody(request.body)
            }
            println(res.bodyAsText())
            res.body<T>()
        } catch (e: Exception) {
            println("Exception during tg bot api call: ${e.message} ${e.stackTraceToString()}")
            null
        }
    }
}