package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientsMeasureInputDto(
    val input: List<Ingredient>
) {
    @Serializable
    data class Ingredient (
        val id: Int,
        val count: Double
    )

    data class IngredientOnProcess(
        var count: Double,
        var bound: Double,
        var measureId: Int? = null,
        var measureName: String? = null
    )

    fun getIds(): List<Int> = input.map { it.id }

    //Returns map IngredientId -> IngredientCount + Current bound + Measure for easily counting topBounds
    fun toMap(): Map<Int, IngredientOnProcess> = input.associate {
        Pair(it.id, IngredientOnProcess(it.count, it.count * 2))
    }
}