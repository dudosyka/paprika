package com.paprika.dto.user

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserEatingsParamsInputDto (
    val name: String,
    val size: Double,
    @Transient val type: Int = 1,
    val difficulty: Int,
    val dishCount: Int
)