package com.paprika.database.models.ingredient

import com.paprika.utils.database.BaseIntIdTable

object IngredientModel: BaseIntIdTable() {
    val name = text("name")
    val measureType = integer("measure_type")
    val cellulose = double("cellulose")
}