package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class MicronutrientsDto (
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val cellulose: Double
)