package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class DishTypeOutputDto (
    val id: Int,
    val name: String
)