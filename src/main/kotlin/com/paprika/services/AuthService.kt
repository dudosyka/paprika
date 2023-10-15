package com.paprika.services

import com.paprika.dto.user.AuthOutputDto
import com.paprika.plugins.createToken
import com.paprika.utils.database.idValue
import com.paprika.utils.kodein.KodeinService
import org.kodein.di.DI
import org.kodein.di.instance

class AuthService(di: DI) : KodeinService(di) {
    private val userService: UserService by instance()
    fun authTelegramUser(telegramId: Int): AuthOutputDto? {
        val user = userService.getUserByTelegram(telegramId)
        return if (user != null)
            AuthOutputDto(createToken(mutableMapOf(
                "id" to user.idValue.toString(),
                "telegramId" to telegramId.toString()
            )))
        else
            null
    }
}