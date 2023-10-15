package com.paprika.database.models.user

import com.paprika.utils.database.BaseIntIdTable

object UserEatingsParamsModel: BaseIntIdTable() {
    val user = reference("user", UserModel)
    val name = text("name")
    val size = integer("size")
    val type = text("type")
    val difficulty = integer("difficulty").default(0)
    val dishCount = integer("dish_count").default(0)
}