package com.paprika.controllers

import com.paprika.database.dao.dish.toDto
import com.paprika.dto.EatingOptionsDto
import com.paprika.dto.PaprikaInputDto
import com.paprika.services.DishService
import com.paprika.services.PaprikaService
import com.paprika.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class PaprikaMainController(override val di: DI) : KodeinController() {
    private val dishService: DishService by instance()
    private val paprikaService: PaprikaService by instance()
    override fun Routing.registerRoutes() {
        route("/test") {
            get("/dishes/{dietId}") {
                val data = call.receive<EatingOptionsDto>()
                val diet = call.parameters["dietId"]?.toIntOrNull() ?: throw Exception("Bad request")
                call.respond(dishService.getDishesByEatingParams(data, diet).toDto())
            }
            get("/paprika") {
                val data = call.receive<PaprikaInputDto>()
                call.respond(paprikaService.calculateMenu(data))
            }
        }
    }
}