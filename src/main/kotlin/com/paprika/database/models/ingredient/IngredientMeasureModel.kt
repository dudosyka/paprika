package com.paprika.database.models.ingredient

import com.paprika.utils.database.BaseIntIdTable

object IngredientMeasureModel: BaseIntIdTable() {
    val ingredient = reference("ingredient", IngredientModel)
    val measure = reference("measure", MeasureModel)
    val topBound = double("top_bound")
}