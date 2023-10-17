package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaprikaInputDto (
    val calories: Double? = null,
    val idealMicronutrients: ParametersDto? = null,
    val diet: Int,
    val eatings: List<EatingOptionsDto>,
//    val portionsCount: Int,
    val excludeDishes: List<Int> = listOf()
)