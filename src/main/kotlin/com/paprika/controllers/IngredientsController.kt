package com.paprika.controllers

import com.paprika.dto.IngredientsMeasureInputDto
import com.paprika.services.IngredientService
import com.paprika.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class IngredientsController(override val di: DI) : KodeinController() {
    private val ingredientService: IngredientService by instance()
    override fun Routing.registerRoutes() {
        route("measures") {
            post {
                val input = call.receive<IngredientsMeasureInputDto>()
                call.respond(ingredientService.getCorrectMeasures(input))
            }
        }
    }
}