package com.paprika.database.models.user

import com.paprika.database.models.dish.DishTypeModel
import com.paprika.utils.database.BaseIntIdTable

object UserEatingsParamsModel: BaseIntIdTable() {
    val user = reference("user", UserModel)
    val name = text("name")
    val size = double("size")
    val type = reference("type", DishTypeModel)
    val difficulty = integer("difficulty").default(0)
    val dishCount = integer("dish_count").default(0)
}