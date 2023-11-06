package com.paprika.database.dao.user

import com.paprika.database.models.user.UserModel
import com.paprika.database.models.user.UserParamsModel
import com.paprika.dto.user.CreateUserDto
import com.paprika.dto.user.UserOutputDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserDao(id: EntityID<Int>): BaseIntEntity(id, UserModel) {
    companion object : BaseIntEntityClass<UserDao>(UserModel)

    var telegramId by UserModel.telegramId
    var sex by UserModel.sex
    var height by UserModel.height
    var weight by UserModel.weight
    var birthday by UserModel.birthday
    val age: Int
        get() = birthday
    var active by UserModel.active

    val params by UserParamsDao backReferencedOn UserParamsModel.user

    fun toDto(): UserOutputDto =
        UserOutputDto(telegramId, sex, height, weight, age, active)

    fun fromDto(data: CreateUserDto) {
        sex = data.sex
        height = data.height
        weight = data.weight
        birthday = data.birthday
        active = data.active
    }
}