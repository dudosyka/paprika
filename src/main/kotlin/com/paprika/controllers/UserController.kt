package com.paprika.controllers

import com.paprika.dto.EatingOutputDto
import com.paprika.dto.user.AuthOutputDto
import com.paprika.dto.user.UserOutputDto
import com.paprika.dto.user.UserParamsInputDto
import com.paprika.dto.user.UserParamsOutputDto
import com.paprika.exceptions.BadRequestException
import com.paprika.exceptions.UnauthorizedException
import com.paprika.services.AuthService
import com.paprika.services.CacheService
import com.paprika.services.UserService
import com.paprika.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class UserController(override val di: DI) : KodeinController() {
    private val userService: UserService by instance()
    private val authService: AuthService by instance()
    private val cacheService: CacheService by instance()
    override fun Routing.registerRoutes() {
        route("user") {
            route("auth/telegram/{telegramId}") {
                get {
                    val telegramId = call.parameters["telegramId"]?.toIntOrNull() ?: throw BadRequestException("You must provide telegramId param")
                    val user = authService.authTelegramUser(telegramId)
                    if (user != null)
                        call.respond<AuthOutputDto>(user)
                    else
                        throw UnauthorizedException("User not found")
                }
                post {
                    val telegramId = call.parameters["telegramId"]?.toIntOrNull() ?: throw BadRequestException("You must provide telegramId param")
                    call.respond<AuthOutputDto>(userService.createUser(telegramId, call.receive()))
                }
            }
            authenticate("authorized") {
                get {
                    val authorizedUser = getAuthorized(call)
                    call.respond<UserOutputDto>(userService.getUser(authorizedUser).toDto())
                }
                patch {
                    val authorizedUser = getAuthorized(call)
                    val user = userService.updateUser(authorizedUser, call.receive())
                    call.respond<UserOutputDto>(user)
                }
                route("params") {
                    get {
                        val authorizedUser = getAuthorized(call)
                        call.respond<UserParamsOutputDto>(userService.getUserParamsAsDto(authorizedUser))
                    }
                    post {
                        val authorizedUser = getAuthorized(call)
                        val userParamsOutputDto = call.receive<UserParamsInputDto>()
                        call.respond<UserParamsOutputDto>(userService.setUserParams(authorizedUser, userParamsOutputDto))
                    }
                }
                route("cache") {
                    get {
                        val authorizedUser = getAuthorized(call)
                        call.respond<List<EatingOutputDto>>(cacheService.loadUserSaved(authorizedUser.id))
                    }
                }
            }
        }
    }
}