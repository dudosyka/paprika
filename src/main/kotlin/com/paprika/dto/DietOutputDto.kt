package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class DietOutputDto (
    val id: Int,
    val name: String,
)