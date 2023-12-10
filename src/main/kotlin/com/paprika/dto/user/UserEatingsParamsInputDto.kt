package com.paprika.dto.user

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/*
    * DTO for setting up user` eatings params
    * Eating params - objects that configures params for each eating of the daily menu.
 */
@Serializable
data class UserEatingsParamsInputDto (
    val name: String,
    val size: Double,
    @Transient val type: Int = 1,
    val difficulty: Int,
    val dishCount: Int
)