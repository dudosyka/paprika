package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserOutputDto (
    val telegramId: Int,
    val sex: Int,
    val height: Double,
    val weight: Double,
    val age: Int,
    val active: Int
)