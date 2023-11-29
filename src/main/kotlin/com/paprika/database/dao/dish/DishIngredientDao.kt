package com.paprika.database.dao.dish

import com.paprika.database.dao.ingredient.IngredientDao
import com.paprika.database.dao.ingredient.MeasureDao
import com.paprika.database.models.dish.DishIngredientModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DishIngredientDao(id: EntityID<Int>): BaseIntEntity(id, DishIngredientModel) {
    companion object: BaseIntEntityClass<DishIngredientDao>(DishIngredientModel)

    val dish by DishDao referencedOn DishIngredientModel.dish
    val ingredient by IngredientDao referencedOn DishIngredientModel.ingredient
    val measure by MeasureDao referencedOn DishIngredientModel.measure
    val measureCount by DishIngredientModel.measureCount
}