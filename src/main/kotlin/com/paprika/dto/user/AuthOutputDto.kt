package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class AuthOutputDto (
    val token: String,
    val newUser: UserOutputDto? = null
)