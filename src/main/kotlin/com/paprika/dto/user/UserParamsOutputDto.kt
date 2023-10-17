package com.paprika.dto.user

import com.paprika.dto.EatingOptionsDto
import com.paprika.dto.PaprikaInputDto
import com.paprika.dto.ParametersDto
import kotlinx.serialization.Serializable

@Serializable
data class UserParamsOutputDto (
    val diet: Int,
    val calories: Double,
    val isMacronutrientsParamsSet: Boolean = false,
    val params: ParametersDto,
    var eatingsParams: List<UserEatingsParamsOutputDto> = listOf()
) {
    fun toPaprikaInput(): PaprikaInputDto =
        PaprikaInputDto(
            calories,
            params,
            diet,
            eatingsParams.map { EatingOptionsDto(it.name, it.size, it.type.id, it.difficulty, it.dishCount) }
        )
}