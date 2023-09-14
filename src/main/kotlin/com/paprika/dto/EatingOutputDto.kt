package com.paprika.dto

data class EatingOutputDto (
    val name: String,
    val micronutrients: MicronutrientsDto,
    val idealMicronutrients: MicronutrientsDto,
)