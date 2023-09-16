package com.paprika.dto

import com.paprika.database.models.ingredient.IngredientMeasureModel
import kotlinx.serialization.Serializable

@Serializable
data class MeasureDto (
    val name: String,
    val nameFiveItems: String,
    val nameFractional: String,
    val nameTwoItems: String,
    val isDimensional: Boolean
)