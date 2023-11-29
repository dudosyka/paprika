package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val measureType: MeasureDto,
    val measureCount: Double,
)
