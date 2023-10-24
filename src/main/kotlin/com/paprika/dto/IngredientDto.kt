package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val id: Int,
    val name: String,
    val measureType: Int,
    val measureCount: Double,
)
