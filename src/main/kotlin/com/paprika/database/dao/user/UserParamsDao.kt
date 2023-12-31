package com.paprika.database.dao.user

import com.paprika.database.dao.dish.DietDao
import com.paprika.database.models.user.UserParamsModel
import com.paprika.dto.ParametersDto
import com.paprika.dto.user.UserEatingsParamsOutputDto
import com.paprika.dto.user.UserParamsOutputDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import com.paprika.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class UserParamsDao(id: EntityID<Int>): BaseIntEntity(id, UserParamsModel) {
    companion object: BaseIntEntityClass<UserParamsDao>(UserParamsModel)

    var user by UserDao referencedOn UserParamsModel.user
    var diet by DietDao optionalReferencedOn UserParamsModel.diet

    var calories by UserParamsModel.calories

    var isMacronutrientsParamsSet by UserParamsModel.isMacronutrientsParamsSet

    var minProtein by UserParamsModel.minProtein
    var maxProtein by UserParamsModel.maxProtein

    var minFat by UserParamsModel.minFat
    var maxFat by UserParamsModel.maxFat

    var minCarbohydrates by UserParamsModel.minCarbohydrates
    var maxCarbohydrates by UserParamsModel.maxCarbohydrates

    var minCellulose by UserParamsModel.minCellulose
    var maxCellulose by UserParamsModel.maxCellulose

    fun toOutputDto(eatingsParams: List<UserEatingsParamsOutputDto> = listOf()) =
        UserParamsOutputDto(diet?.idValue ?: 0, calories, isMacronutrientsParamsSet, ParametersDto(calories, minProtein, maxProtein, minFat, maxFat, minCarbohydrates, maxCarbohydrates, minCellulose, maxCellulose), eatingsParams)
}