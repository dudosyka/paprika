package com.paprika.database.dao.user

import com.paprika.database.dao.dish.DietDao
import com.paprika.database.models.user.UserParamsModel
import com.paprika.dto.user.UserEatingsParamsDto
import com.paprika.dto.user.UserParamsDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import com.paprika.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class UserParamsDao(id: EntityID<Int>): BaseIntEntity(id, UserParamsModel) {
    companion object: BaseIntEntityClass<UserParamsDao>(UserParamsModel)

    var user by UserDao referencedOn UserParamsModel.user
    var diet by DietDao referencedOn UserParamsModel.diet

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

    fun toDto(eatingsParams: List<UserEatingsParamsDto> = listOf()) =
        UserParamsDto(diet.idValue, calories, isMacronutrientsParamsSet, minProtein, maxProtein, minFat, maxFat, minCarbohydrates, maxCarbohydrates, minCellulose, maxCellulose, eatingsParams)
}