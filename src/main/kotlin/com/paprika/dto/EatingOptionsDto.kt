package com.paprika.dto

import kotlinx.serialization.Serializable

@Serializable
data class EatingOptionsDto (
    val name: String,
    val size: Double,
    val type: Int,
    val time: Int,
    /*
        * super easy = 1
        * easy = 2
        * middle = 3
        * hard = 4
    */
    val difficulty: Int,
    val dishCount: Int
)