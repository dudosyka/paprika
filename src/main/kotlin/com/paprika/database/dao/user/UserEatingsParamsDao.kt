package com.paprika.database.dao.user

import com.paprika.database.models.user.UserEatingsParamsModel
import com.paprika.dto.user.UserEatingsParamsDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEatingsParamsDao(id: EntityID<Int>): BaseIntEntity(id, UserEatingsParamsModel) {
    companion object: BaseIntEntityClass<UserEatingsParamsDao>(UserEatingsParamsModel)

    val user by UserDao referencedOn UserEatingsParamsModel.user
    val name by UserEatingsParamsModel.name
    val size by UserEatingsParamsModel.size
    val type by UserEatingsParamsModel.type
    val difficulty by UserEatingsParamsModel.difficulty
    val dishCount by UserEatingsParamsModel.dishCount
    fun toDto() =
        UserEatingsParamsDto(name, size, type.split(",").map { it.toInt() }, difficulty, dishCount)

}
fun List<UserEatingsParamsDao>.toDto(): List<UserEatingsParamsDto> {
    return this.map { it.toDto() }
}
