package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class ParametersInputDto (
    val calories: Double,

    val minProtein: Double,
    val maxProtein: Double,

    val minFat: Double,
    val maxFat: Double,

    val minCarbohydrates: Double,
    val maxCarbohydrates: Double,

    val minCellulose: Double,
    val maxCellulose: Double,
) {
}