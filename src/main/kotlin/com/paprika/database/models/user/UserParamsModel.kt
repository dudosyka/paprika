package com.paprika.database.models.user

import com.paprika.database.models.dish.DietModel
import com.paprika.utils.database.BaseIntIdTable

object UserParamsModel: BaseIntIdTable() {
    val user = reference("user", UserModel)
    val diet = optReference("diet", DietModel).default(null)

    val calories = double("calories")

    val isMacronutrientsParamsSet = bool("is_macronutrients_params_set").default(false)

    val minProtein = double("min_protein").default(0.0)
    val maxProtein = double("max_protein").default(0.0)

    val minFat = double("minFat").default(0.0)
    val maxFat = double("maxFat").default(0.0)

    val minCarbohydrates = double("minCarbohydrates").default(0.0)
    val maxCarbohydrates = double("maxCarbohydrates").default(0.0)

    val minCellulose = double("minCellulose").default(0.0)
    val maxCellulose = double("maxCellulose").default(0.0)
}