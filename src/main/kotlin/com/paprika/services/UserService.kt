package com.paprika.services

import com.paprika.database.dao.dish.DietDao
import com.paprika.database.dao.user.UserDao
import com.paprika.database.dao.user.UserEatingsParamsDao
import com.paprika.database.dao.user.UserParamsDao
import com.paprika.database.dao.user.toDto
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
    fun getUserParamsAsDto(authorizedUser: AuthorizedUser): UserParamsDto = transaction {
        val userParams = getUserParams(authorizedUser)
        val userEatingsParams = getUserEatingParams(authorizedUser)

        userParams.toDto(userEatingsParams.toDto())
    }
    fun setUserParams(authorizedUser: AuthorizedUser, userParamsDto: UserParamsDto): UserParamsDto = transaction {
        val userParams = try {
            val userParams = getUserParams(authorizedUser)
            userParams.diet = DietDao[userParamsDto.diet]
            userParams.calories = userParamsDto.calories
            userParams.isMacronutrientsParamsSet = false
            if (userParamsDto.isMacronutrientsParamsSet) {
                userParams.isMacronutrientsParamsSet = true
                userParams.minProtein = userParamsDto.minProtein
                userParams.maxProtein = userParamsDto.maxProtein
                userParams.minFat = userParamsDto.minFat
                userParams.maxFat = userParamsDto.maxFat
                userParams.minCarbohydrates = userParamsDto.minCarbohydrates
                userParams.maxCarbohydrates = userParamsDto.maxCarbohydrates
                userParams.minCellulose = userParamsDto.minCellulose
                userParams.maxCellulose = userParamsDto.maxCellulose
            }
            userParams.flush()
            userParams
        } catch (_: Exception) {
            UserParamsDao.new {
                user = UserDao[authorizedUser.id]
                diet = DietDao[userParamsDto.diet]
                calories = userParamsDto.calories
                if (userParamsDto.isMacronutrientsParamsSet) {
                    isMacronutrientsParamsSet = true
                    minProtein = userParamsDto.minProtein
                    maxProtein = userParamsDto.maxProtein
                    minFat = userParamsDto.minFat
                    maxFat = userParamsDto.maxFat
                    minCarbohydrates = userParamsDto.minCarbohydrates
                    maxCarbohydrates = userParamsDto.maxCarbohydrates
                    minCellulose = userParamsDto.minCellulose
                    maxCellulose = userParamsDto.maxCellulose
                }
            }
        }


        UserEatingsParamsModel.deleteWhere { user eq authorizedUser.id }
        val eatingsParamsDao = UserEatingsParamsModel.batchInsert(userParamsDto.eatingsParams) {
            this[UserEatingsParamsModel.user] = authorizedUser.id
            this[UserEatingsParamsModel.name] = it.name
            this[UserEatingsParamsModel.size] = it.size
            this[UserEatingsParamsModel.type] = it.type.joinToString(",")
            this[UserEatingsParamsModel.difficulty] = it.difficulty
            this[UserEatingsParamsModel.dishCount] = it.dishCount
        }.map {
            UserEatingsParamsDao.wrapRow(it)
        }

        userParams.toDto(eatingsParamsDao.toDto())
    }
}