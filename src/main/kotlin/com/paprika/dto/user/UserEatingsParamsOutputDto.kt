package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserEatingsParamsOutputDto (
    val name: String,
    val size: Int,
    val type: List<Int>,
    val difficulty: Int,
    val dishCount: Int
)