package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDto (
    val sex: Int,
    val height: Double,
    val weight: Double,
    val birthday: Int,
    val active: Int
)