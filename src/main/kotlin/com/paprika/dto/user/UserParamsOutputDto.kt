package com.paprika.dto.user

import com.paprika.dto.EatingOptionsDto
import com.paprika.dto.PaprikaInputDto
import com.paprika.dto.ParametersDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserParamsOutputDto (
    @Transient val diet: Int = 1,
    val calories: Double,
    val isMacronutrientsParamsSet: Boolean = false,
    val params: ParametersDto,
    var eatings: List<UserEatingsParamsOutputDto> = listOf()
) {
    fun toPaprikaInput(): PaprikaInputDto =
        PaprikaInputDto(
            calories,
            params,
            diet,
            eatings.map { EatingOptionsDto(it.name, it.size, it.type, it.difficulty, it.dishCount) }
        )
}