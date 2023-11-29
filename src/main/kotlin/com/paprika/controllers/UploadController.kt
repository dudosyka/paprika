package com.paprika.controllers

import com.paprika.dto.upload.DatabaseStatisticOutputDto
import com.paprika.exceptions.BadRequestException
import com.paprika.services.DataManagerService
import com.paprika.utils.kodein.KodeinController
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class UploadController(override val di: DI) : KodeinController() {
    private val dataManagerService: DataManagerService by instance()

    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */

    private suspend fun receiveFile(
        multipartData: MultiPartData,
        files: MutableMap<String, ByteArray?>
    ): Pair<Boolean, Map<String, ByteArray>> {
        var rewrite = false
        multipartData.forEachPart {
            when (it) {
                is PartData.FileItem -> {
                    val bytes = it.streamProvider().readBytes()
                    if (files.keys.contains(it.name))
                        files[it.name ?: "__KEY"] = bytes
                }


                is PartData.FormItem -> {
                    if (it.name == "rewrite")
                        rewrite = true
                }

                else -> {}
            }
        }

        if (!files.all { it.value != null })
            throw BadRequestException("You must provide ${files.size} files (${files.keys.joinToString(",") { it }}) to start loading data")

        return Pair(rewrite, files.map { Pair(it.key, it.value!!) }.toMap())
    }


    override fun Routing.registerRoutes() {
//        authenticate("authorized") {
            route("menu") {
                route("upload") {
                    post("measures") {
                        val data = receiveFile(call.receiveMultipart(), mutableMapOf("measures" to null))
                        call.respond(
                            dataManagerService.uploadMeasures(data.second["measures"]!!, data.first)
                        )
                    }
                    post("ingredients") {
                        val data = receiveFile(call.receiveMultipart(), mutableMapOf("ingredients" to null))
                        call.respond(
                            dataManagerService.uploadIngredients(data.second["ingredients"]!!, data.first)
                        )
                    }
                    post("dishes") {
                        val data = receiveFile(
                            call.receiveMultipart(),
                            mutableMapOf("dishes" to null, "dish-to-ingredient" to null, "dish-steps" to null, "dish-categories" to null)
                        )
                        call.respond(
                            dataManagerService.uploadDishes(data.second["dishes"]!!, data.second["dish-to-ingredient"]!!, data.second["dish-steps"]!!, data.second["dish-categories"]!!, data.first)
                        )
                    }
                }
                get {
                    call.respond<DatabaseStatisticOutputDto>(dataManagerService.getStatistic())
                }
            }
//        }
    }
}