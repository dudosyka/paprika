package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class EatingOutputDto (
    val name: String,
    val dishes: List<DishDto>,
    val micronutrients: MicronutrientsDto,
    val idealMicronutrients: MicronutrientsDto,
)