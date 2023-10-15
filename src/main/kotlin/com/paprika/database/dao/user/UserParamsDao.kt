package com.paprika.database.dao.user

import com.paprika.database.dao.dish.DietDao
import com.paprika.database.models.user.UserParamsModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserParamsDao(id: EntityID<Int>): BaseIntEntity(id, UserParamsModel) {
    companion object: BaseIntEntityClass<UserParamsDao>(UserParamsModel)

    val user by UserDao referencedOn UserParamsModel.user
    val diet by DietDao referencedOn UserParamsModel.diet

    val calories by UserParamsModel.calories

    val isMacronutrientsParamsSet by UserParamsModel.isMacronutrientsParamsSet

    val minProtein by UserParamsModel.minProtein
    val maxProtein by UserParamsModel.maxProtein

    val minFat by UserParamsModel.minFat
    val maxFat by UserParamsModel.maxFat

    val minCarbohydrates by UserParamsModel.minCarbohydrates
    val maxCarbohydrates by UserParamsModel.maxCarbohydrates
}