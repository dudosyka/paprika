package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaprikaInputDto (
    val calories: Double?,
    val idealMicronutrients: ParametersInputDto?,
    val diet: Int,
    val eatings: List<EatingOptionsDto>,
    val portionsCount: Int,
    val excludeDishes: List<Int>
)