package com.paprika.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MacronutrientsDto (
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    @Transient val cellulose: Double = 0.0
)