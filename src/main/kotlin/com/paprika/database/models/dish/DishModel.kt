package com.paprika.database.models.dish

import com.paprika.utils.database.BaseIntIdTable

object DishModel: BaseIntIdTable() {
    val name = text("name")
    val calories = integer("calories")
    val protein = integer("protein")
    val fat = integer("fat")
    val carbohydrates = integer("carbohydrates")
    val cellulose = integer("cellulose")
    val weight = integer("weight")
    val diet = reference("diet", DietModel)
    val type = reference("type", DishTypeModel)
}