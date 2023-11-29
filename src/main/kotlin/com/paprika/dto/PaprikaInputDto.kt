package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaprikaInputDto (
    val calories: Double? = null,
    val params: ParametersDto? = null,
    val diet: Int = 1,
    val eatings: List<EatingOptionsDto>,
    var excludeDishes: List<Int> = listOf()
)