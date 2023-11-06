package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaprikaInputDto (
    val calories: Double? = null,
    val params: ParametersDto? = null,
    val diet: Int,
    val eatings: List<EatingOptionsDto>,
    val excludeDishes: List<Int> = listOf()
)