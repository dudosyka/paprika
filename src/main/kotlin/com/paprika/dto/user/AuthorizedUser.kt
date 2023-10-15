package com.paprika.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizedUser (
    val id: Int,
    val telegramId: Int,
)