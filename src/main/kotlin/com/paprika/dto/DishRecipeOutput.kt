package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class DishRecipeOutput (
    val id: Int,
    val ingredients: List<IngredientDto>,
    val steps: List<DishRecipeStepDto>
)