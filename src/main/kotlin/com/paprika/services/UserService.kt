package com.paprika.services

import com.paprika.database.dao.user.UserDao
import com.paprika.database.models.user.UserModel
import com.paprika.dto.user.*
import com.paprika.exceptions.BadRequestException
import com.paprika.exceptions.ForbiddenException
import com.paprika.exceptions.NotFoundException
import com.paprika.plugins.createToken
import com.paprika.utils.database.idValue
import com.paprika.utils.kodein.KodeinService
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI

class UserService(di: DI) : KodeinService(di) {
    fun getUser(authorizedUser: AuthorizedUser): UserDao = transaction {
        try {
            UserDao[authorizedUser.id]
        } catch (_: Exception) {
            throw ForbiddenException()
        }
    }
    fun getUserByTelegram(telegramId: Int): UserDao? = transaction {
        val user = UserDao.searchQuery(UserModel.telegramId eq telegramId).map { UserDao.wrapRow(it) }
        if (user.isNotEmpty()) {
            user.first()
        }
        else
            null
    }
    fun createUser(telegramId: Int, createUserDto: CreateUserDto): AuthOutputDto = transaction {
        if (getUserByTelegram(telegramId) != null)
            throw BadRequestException()

        val user = UserDao.new {
            this.telegramId = telegramId
            sex = createUserDto.sex
            height = createUserDto.height
            weight = createUserDto.weight
            birthday = createUserDto.birthday
            active = createUserDto.active
        }


        println(user.idValue)
        println(user.idValue.toString())
        AuthOutputDto(
            createToken(mutableMapOf(
                "id" to user.idValue.toString(),
                "telegramId" to telegramId.toString()
            )),
            user.toDto()
        )
    }
    fun updateUser(authorizedUser: AuthorizedUser, createUserDto: CreateUserDto): UserOutputDto = transaction {
        getUserByTelegram(authorizedUser.telegramId).run {
            if (this == null)
                throw NotFoundException("User not found")
            else {
                fromDto(createUserDto)
                flush()
                toDto()
            }
        }

    }
//    fun getUserParams(authorizedUser: AuthorizedUser): UserParamsOutputDto {
//
//    }
}