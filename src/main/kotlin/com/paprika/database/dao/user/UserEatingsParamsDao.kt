package com.paprika.database.dao.user

import com.paprika.database.dao.dish.DishTypeDao
import com.paprika.database.models.user.UserEatingsParamsModel
import com.paprika.dto.user.UserEatingsParamsOutputDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import com.paprika.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class UserEatingsParamsDao(id: EntityID<Int>): BaseIntEntity(id, UserEatingsParamsModel) {
    companion object: BaseIntEntityClass<UserEatingsParamsDao>(UserEatingsParamsModel)

    val user by UserDao referencedOn UserEatingsParamsModel.user
    val name by UserEatingsParamsModel.name
    val size by UserEatingsParamsModel.size
    val type by DishTypeDao optionalReferencedOn UserEatingsParamsModel.type
    val difficulty by UserEatingsParamsModel.difficulty
    val dishCount by UserEatingsParamsModel.dishCount
    fun toOutputDto() =
        UserEatingsParamsOutputDto(name, size, type?.idValue ?: 0, difficulty, dishCount)

}
fun List<UserEatingsParamsDao>.toOutputDto(): List<UserEatingsParamsOutputDto> {
    return this.map { it.toOutputDto() }
}
