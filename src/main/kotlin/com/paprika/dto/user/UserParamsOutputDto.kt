package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserParamsOutputDto (
    val calories: Double,
    val isMacronutrientsParamsSet: Boolean,
    val minProtein: Double,
    val maxProtein: Double,
    val minFat: Double,
    val maxFat: Double,
    val minCarbohydrates: Double,
    val maxCarbohydrates: Double,
    var eatingsParams: List<UserEatingsParamsOutputDto> = listOf()
)