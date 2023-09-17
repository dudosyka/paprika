package com.paprika.controllers

import com.paprika.utils.generator.DishGenerator
import com.paprika.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.kodein.di.DI

class GeneratorController(override val di: DI) : KodeinController() {
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        route("generator") {
            route("g") {
                get("dished/{count}") {
                    DishGenerator(count = call.parameters["count"]?.toInt() ?: 0)
                }
            }
        }
    }

}