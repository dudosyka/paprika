package com.paprika.dto.user

import com.paprika.dto.EatingOptionsDto
import com.paprika.dto.PaprikaInputDto
import com.paprika.dto.ParametersInputDto
import kotlinx.serialization.Serializable

@Serializable
data class UserParamsDto (
    val diet: Int,
    val calories: Double,
    val isMacronutrientsParamsSet: Boolean = false,
    val minProtein: Double = 0.0,
    val maxProtein: Double = 0.0,
    val minFat: Double = 0.0,
    val maxFat: Double = 0.0,
    val minCarbohydrates: Double = 0.0,
    val maxCarbohydrates: Double = 0.0,
    val minCellulose: Double = 0.0,
    val maxCellulose: Double = 0.0,
    var eatingsParams: List<UserEatingsParamsDto> = listOf()
) {
    fun toPaprikaInput(): PaprikaInputDto =
        PaprikaInputDto(
            calories,
            ParametersInputDto(
                calories,
                minProtein,
                maxProtein,
                minFat,
                maxFat,
                minCarbohydrates,
                maxCarbohydrates,
                minCellulose,
                maxCellulose
            ),
            diet,
            eatingsParams.map { EatingOptionsDto(it.name, it.size, it.type.first(), it.difficulty, it.dishCount) }
        )
}