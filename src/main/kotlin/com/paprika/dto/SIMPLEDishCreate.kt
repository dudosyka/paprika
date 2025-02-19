package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class SIMPLEDishCreate (
    val protein: Double,
    val fats: Double,
    val carbo: Double,
    val calories: Double,
    val image: String,
    val description: String,
    val name: String,
    val portions: Int,
    val time: Int,
    val diet: Int,
    val type: Int,
)