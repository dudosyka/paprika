package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientsMeasureOutputDto (
    private var output: List<Ingredient>
) {
    constructor(map: Map<Int, IngredientsMeasureInputDto.IngredientOnProcess>): this(listOf()) {
        output = map.map {
            Ingredient(
                id = it.key,
                measure = it.value.measure ?: 0
            )
        }
    }
    @Serializable
    data class Ingredient (
        val id: Int,
        val measure: Int,
    )
}