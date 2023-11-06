package com.paprika.services

import com.paprika.database.dao.dish.DietDao
import com.paprika.database.dao.user.UserDao
import com.paprika.database.dao.user.UserEatingsParamsDao
import com.paprika.database.dao.user.UserParamsDao
import com.paprika.database.dao.user.toOutputDto
import com.paprika.database.models.user.UserEatingsParamsModel
import com.paprika.database.models.user.UserModel
import com.paprika.database.models.user.UserParamsModel
import com.paprika.dto.user.*
import com.paprika.exceptions.BadRequestException
import com.paprika.exceptions.ForbiddenException
import com.paprika.exceptions.NotFoundException
import com.paprika.plugins.createToken
import com.paprika.utils.database.idValue
import com.paprika.utils.kodein.KodeinService
import com.paprika.utils.params.ParamsManager
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
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
    fun getUserParams(authorizedUser: AuthorizedUser): UserParamsDao = transaction {
        val userParams = UserParamsDao.searchQuery(UserParamsModel.user eq authorizedUser.id).map { UserParamsDao.wrapRow(it) }

        if (userParams.isEmpty())
            throw NotFoundException()

        userParams.first()
    }
    fun getUserEatingParams(authorizedUser: AuthorizedUser): List<UserEatingsParamsDao> = transaction {
        val userEatingsParams = UserEatingsParamsDao.searchQuery(UserEatingsParamsModel.user eq authorizedUser.id).map { UserEatingsParamsDao.wrapRow(it) }

        userEatingsParams
    }
    fun getUserParamsAsDto(authorizedUser: AuthorizedUser): UserParamsOutputDto = transaction {
        val userParams = getUserParams(authorizedUser)
        val userEatingsParams = getUserEatingParams(authorizedUser)

        userParams.toOutputDto(userEatingsParams.toOutputDto())
    }

    private fun setUserParams(userParamsInputDto: UserParamsInputDto): UserParamsDao.() -> Unit {
        return {
            diet = getDiet(userParamsInputDto.diet)
            calories = userParamsInputDto.calories
            if (userParamsInputDto.isMacronutrientsParamsSet && userParamsInputDto.params != null) {
                ParamsManager.process { validateParams(userParamsInputDto.params) }
                isMacronutrientsParamsSet = true
                minProtein = userParamsInputDto.params.minProtein
                maxProtein = userParamsInputDto.params.maxProtein
                minFat = userParamsInputDto.params.minFat
                maxFat = userParamsInputDto.params.maxFat
                minCarbohydrates = userParamsInputDto.params.minCarbohydrates
                maxCarbohydrates = userParamsInputDto.params.maxCarbohydrates
                minCellulose = userParamsInputDto.params.minCellulose
                maxCellulose = userParamsInputDto.params.maxCellulose
            }
        }
    }

    private fun getType(type: Int): Int? = if (type == 0) null else type

    private fun getDiet(diet: Int): DietDao? = if (diet == 0) null else transaction { DietDao[diet] }

    fun setUserParams(authorizedUser: AuthorizedUser, userParamsInputDto: UserParamsInputDto): UserParamsOutputDto = transaction {
        val userParams = try {
            val userParams = getUserParams(authorizedUser)
            userParams.apply(setUserParams(userParamsInputDto))
            userParams.flush()
            userParams
        } catch (_: Exception) {
            UserParamsDao.new {
                user = UserDao[authorizedUser.id]
                apply(setUserParams(userParamsInputDto))
            }
        }


        UserEatingsParamsModel.deleteWhere { user eq authorizedUser.id }
        val eatingsParamsDao = UserEatingsParamsModel.batchInsert(userParamsInputDto.eatings) {
            this[UserEatingsParamsModel.user] = authorizedUser.id
            this[UserEatingsParamsModel.name] = it.name
            this[UserEatingsParamsModel.size] = it.size
            this[UserEatingsParamsModel.type] = getType(it.type)
            this[UserEatingsParamsModel.difficulty] = it.difficulty
            this[UserEatingsParamsModel.dishCount] = it.dishCount
        }.map {
            UserEatingsParamsDao.wrapRow(it)
        }

        userParams.toOutputDto(eatingsParamsDao.toOutputDto())
    }
}