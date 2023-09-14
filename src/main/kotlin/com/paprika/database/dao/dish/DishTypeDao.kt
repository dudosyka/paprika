package com.paprika.database.dao.dish

import com.paprika.database.models.dish.DietModel
import com.paprika.database.models.dish.DishTypeModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DishTypeDao(id : EntityID<Int>) : BaseIntEntity(id, DishTypeModel) {
    companion object : BaseIntEntityClass<DishTypeDao>(DishTypeModel)

    val name by DietModel.name
}