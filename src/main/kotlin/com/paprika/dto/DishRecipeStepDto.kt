package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class DishRecipeStepDto (
    val text: String,
    val imageUrl: String,
    val relativeId: Int
)