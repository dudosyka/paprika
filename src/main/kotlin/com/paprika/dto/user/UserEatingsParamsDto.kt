package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserEatingsParamsDto (
    val name: String,
    val size: Double,
    val type: List<Int>,
    val difficulty: Int,
    val dishCount: Int
)