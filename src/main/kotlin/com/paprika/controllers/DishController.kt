package com.paprika.controllers

import com.paprika.dto.DishRecipeOutput
import com.paprika.dto.DishRecipeStepDto
import com.paprika.dto.SIMPLEDishCreate
import com.paprika.services.DishService
import com.paprika.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.flow
import org.kodein.di.DI
import org.kodein.di.instance

class DishController(override val di: DI) : KodeinController() {
    private val dishService: DishService by instance()
    override fun Routing.registerRoutes() {
        authenticate("authorized") {
            post("recipes") {
                val dishes = call.receive<List<Int>>()
                call.respond(dishService.getDishesRecipe(dishes))
            }
        }
        route("recipe") {
            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                call.respond(dishService.removeSimple(id))
            }
            post("create") {
                val dish = call.receive<SIMPLEDishCreate>()
                call.respond(dishService.createSimple(dish))
            }
            get("all") {
                call.respond(dishService.getDishesSimple())
            }
        }
    }

}