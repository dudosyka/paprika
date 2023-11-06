package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class EatingOutputDto (
    val name: String,
    var dishes: List<DishDto>,
    val params: MicronutrientsDto,
    val idealParams: MicronutrientsDto? = null,
)
