package com.paprika.dto.user

import com.paprika.dto.DishTypeOutputDto
import kotlinx.serialization.Serializable

@Serializable
data class UserEatingsParamsOutputDto (
    val name: String,
    val size: Double,
    val type: DishTypeOutputDto,
    val difficulty: Int,
    val dishCount: Int
)