package com.paprika.dto

import com.paprika.database.models.cache.EatingCacheModel
import kotlinx.serialization.Serializable
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

    val minCellulose: Double,
    val maxCellulose: Double,
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

    }
}