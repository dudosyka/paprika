package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class DishDto (
    val id: Int,
    val name: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val cellulose: Double,
    val weight: Double,
    val timeToCook: Int,
    val dietId: Int,
    val typeId: Int,
    var ingredients: List<IngredientDto> = listOf()
)