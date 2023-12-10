package com.paprika.dto

import com.paprika.database.models.cache.EatingCacheModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class ParametersDto (
    val calories: Double,

    val minProtein: Double,
    val maxProtein: Double,

    val minFat: Double,
    val maxFat: Double,

    val minCarbohydrates: Double,
    val maxCarbohydrates: Double,

    @Transient val minCellulose: Double = 0.0,
    @Transient val maxCellulose: Double = 0.0,
) {
    companion object {
        fun buildFromCache(data: ResultRow): ParametersDto = ParametersDto(
            calories = data[EatingCacheModel.calories],
            minProtein = data[EatingCacheModel.protein],
            maxProtein = data[EatingCacheModel.protein],
            minCellulose = data[EatingCacheModel.cellulose],
            maxCellulose = data[EatingCacheModel.cellulose],
            minFat = data[EatingCacheModel.fat],
            maxFat = data[EatingCacheModel.fat],
            minCarbohydrates = data[EatingCacheModel.carbohydrates],
            maxCarbohydrates = data[EatingCacheModel.carbohydrates]
        )

        fun buildFromMacronutrients(data: MacronutrientsDto) = ParametersDto(
            calories = data.calories,
            minProtein = data.protein,
            maxProtein = data.protein,
            minCellulose = data.cellulose,
            maxCellulose = data.cellulose,
            minFat = data.fat,
            maxFat = data.fat,
            minCarbohydrates = data.carbohydrates,
            maxCarbohydrates = data.carbohydrates
        )
    }
}