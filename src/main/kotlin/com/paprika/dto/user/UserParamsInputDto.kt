package com.paprika.dto.user

import com.paprika.dto.EatingOptionsDto
import com.paprika.dto.PaprikaInputDto
import com.paprika.dto.ParametersDto
import kotlinx.serialization.Serializable

@Serializable
data class UserParamsInputDto (
    val diet: Int,
    val calories: Double,
    val isMacronutrientsParamsSet: Boolean = false,
    val params: ParametersDto? = null,
    var eatingsParams: List<UserEatingsParamsInputDto> = listOf()
)