package com.paprika.controllers

import com.paprika.dto.ExcludedDishesDto
import com.paprika.dto.PaprikaInputDto
import com.paprika.services.PaprikaService
import com.paprika.services.UserService
import com.paprika.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class PaprikaController(override val di: DI) : KodeinController() {
    private val paprikaService: PaprikaService by instance()
    private val userService: UserService by instance()
    override fun Routing.registerRoutes() {
        authenticate("authorized") {
            route("/menu") {
                post("/custom") {
                    val authorizedUser = getAuthorized(call)
                    val data = call.receive<PaprikaInputDto>()
                    call.respond(paprikaService.calculateMenu(authorizedUser, data))
                }
                post("/calculate") {
                    val authorizedUser = getAuthorized(call)
                    val excludedDishesDto = call.receive<ExcludedDishesDto>()
                    val userParams = userService.getUserParamsAsDto(authorizedUser)
                    val paprikaInputDto = userParams.toPaprikaInput()
                    paprikaInputDto.excludeDishes = excludedDishesDto.excluded
                    call.respond(paprikaService.calculateMenu(authorizedUser, paprikaInputDto))
                }
            }
        }
    }
}