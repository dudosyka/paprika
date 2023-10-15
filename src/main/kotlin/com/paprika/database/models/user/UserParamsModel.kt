package com.paprika.database.models.user

import com.paprika.database.models.dish.DietModel
import com.paprika.utils.database.BaseIntIdTable

object UserParamsModel: BaseIntIdTable() {
    val user = reference("user", UserModel)
    val diet = reference("diet", DietModel)

    val calories = double("calories")

    val isMacronutrientsParamsSet = bool("is_macronutrients_params_set")

    val minProtein = double("min_protein")
    val maxProtein = double("max_protein")

    val minFat = double("minFat")
    val maxFat = double("maxFat")

    val minCarbohydrates = double("minCarbohydrates")
    val maxCarbohydrates = double("maxCarbohydrates")
}