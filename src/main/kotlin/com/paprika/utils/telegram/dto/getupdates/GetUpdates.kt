package com.paprika.utils.telegram.dto.getupdates

import com.paprika.utils.telegram.dto.ApiCall
import kotlinx.serialization.Serializable

class GetUpdates(body: Body): ApiCall("getUpdates", body) {
    @Serializable
    data class Body(
        val offset: Long,
        val allowed_updates: List<String>
    )
}