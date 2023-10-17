package com.paprika.dto.upload

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseStatisticOutputDto (
    val lastDishId: Int,
    val lastIngredientId: Int,
    val lastMeasureId: Int,
)