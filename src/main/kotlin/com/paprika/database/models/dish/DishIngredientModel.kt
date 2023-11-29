package com.paprika.database.models.dish

import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.database.models.ingredient.MeasureModel
import com.paprika.utils.database.BaseIntIdTable

object DishIngredientModel: BaseIntIdTable() {
    val dish = reference("dish", DishModel)
    val measure = reference("measure", MeasureModel)
    val ingredient = reference("ingredient", IngredientModel)
    val measureCount = double("measure_count")
}