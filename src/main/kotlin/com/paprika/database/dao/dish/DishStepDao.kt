package com.paprika.database.dao.dish

import com.paprika.database.models.dish.DishStepModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DishStepDao(id: EntityID<Int>): BaseIntEntity(id, DishStepModel) {
    companion object : BaseIntEntityClass<DishStepDao>(DishStepModel)

    val description by DishStepModel.description
    val imageUrl by DishStepModel.imageUrl
    val dish by DishDao referencedOn DishStepModel.dish
    val relativeId by DishStepModel.relative_id
}