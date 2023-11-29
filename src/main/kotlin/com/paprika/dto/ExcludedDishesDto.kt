package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExcludedDishesDto (
    val excluded: List<Int> = listOf()
)