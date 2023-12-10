package com.paprika.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PaprikaOutputDto (
    @Transient val diet: Int = 1,
    val eatings: List<EatingOutputDto>,
    val params: MacronutrientsDto,
    val idealParams: ParametersDto
)