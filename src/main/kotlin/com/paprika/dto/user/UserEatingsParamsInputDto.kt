package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserEatingsParamsInputDto (
    val name: String,
    val size: Double,
    val type: Int,
    val difficulty: Int,
    val dishCount: Int
)