package com.paprika.database.models.ingredient

import com.paprika.utils.database.BaseIntIdTable

object IngredientMeasureModel: BaseIntIdTable() {
    val name = text("name")
    val nameFiveItems = text("name_five_items")
    val nameFractional = text("name_fractional")
    val nameTwoItems = text("name_two_items")
    val isDimensional = bool("is_dimensional")
}