package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaprikaOutputDto (
    val diet: Int,
    val eatings: List<EatingOutputDto>,
    val params: MicronutrientsDto,
    val idealParams: ParametersInputDto
)