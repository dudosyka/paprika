package com.paprika.dto.user

import com.paprika.dto.ParametersDto
import kotlinx.serialization.Serializable

/*
    * DTO class for setting up all user` params includes:
    * eating params
    * size of daily (calories and macronutrients params)
 */
@Serializable
data class UserParamsInputDto (
    val diet: Int = 1,
    val calories: Double,
    val isMacronutrientsParamsSet: Boolean = false,
    val params: ParametersDto? = null,
    var eatings: List<UserEatingsParamsInputDto> = listOf()
)