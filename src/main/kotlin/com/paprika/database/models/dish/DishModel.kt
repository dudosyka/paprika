package com.paprika.database.models.dish

import com.paprika.utils.database.BaseIntIdTable

object DishModel: BaseIntIdTable() {
    val name = text("name")
    val description = text("description")
    val portionsCount = integer("portions_count")
    val imageUrl = text("image_url")
    val calories = double("calories")
    val protein = double("protein")
    val fat = double("fat")
    val carbohydrates = double("carbohydrates")
    val cellulose = double("cellulose").default(0.0)
    val timeToCook = integer("time_to_cook")
    val diet = reference("diet", DietModel)
    val type = reference("type", DishTypeModel)
}