package com.paprika.dto

data class PaprikaInputDto (
    val calories: Int?,
    val idealMicronutrients: MicronutrientsDto?,
    val diet: Int,
    val eatings: List<EatingOptionsDto>,
    val portionsCount: Int,
)