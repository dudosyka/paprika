package com.paprika.database.models.ingredient

import com.paprika.utils.database.BaseIntIdTable

object IngredientModel: BaseIntIdTable() {
    val name = text("name")
    val imageUrl = text("image_url")
    val relativeUrl = text("relative_url")
    val cellulose = double("cellulose").default(0.0)
}