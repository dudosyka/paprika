package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserEatingsParamsOutputDto (
    val name: String,
    val size: Double,
    val type: Int,
    val difficulty: Int,
    val dishCount: Int
)