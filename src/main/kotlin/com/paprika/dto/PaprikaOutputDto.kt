package com.paprika.dto

data class PaprikaOutputDto (
    val diet: Int,
    val eatings: List<EatingOutputDto>,
    val params: MicronutrientsDto,
    val idealParams: MicronutrientsDto
)