package com.paprika.database.dao.ingredient

import com.paprika.database.models.ingredient.IngredientMeasureModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class IngredientMeasureDao(id : EntityID<Int>): BaseIntEntity(id, IngredientMeasureModel) {
    companion object : BaseIntEntityClass<IngredientMeasureDao>(IngredientMeasureModel)

    val name by IngredientMeasureModel.name
    val commonMeasureCount by IngredientMeasureModel.commonMeasureCount
    val commonMeasureType by IngredientMeasureModel.commonMeasureType
}