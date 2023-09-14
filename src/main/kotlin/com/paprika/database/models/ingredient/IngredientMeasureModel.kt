package com.paprika.database.models.ingredient

import com.paprika.utils.database.BaseIntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between

/*
    That model represents ingredients` specific measuring types in common measuring types
    We need this model to relate specific measure system to common measuring: gram for weights and ml for volume of liquids

    For example:
        - Teaspoon of salt -> 5 gram of salt
        - Glass of water -> 250 ml of water

    We will use
        - common_measure_type = 0 for grams
        - common_measure_type = 1 for ml
*/
object IngredientMeasureModel: BaseIntIdTable() {
    val name = text("name")
    val commonMeasureCount = integer("common_measure_count")
    val commonMeasureType = integer("common_measure_type")
}