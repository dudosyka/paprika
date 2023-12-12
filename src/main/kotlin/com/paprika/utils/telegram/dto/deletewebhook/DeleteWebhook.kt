package com.paprika.utils.telegram.dto.deletewebhook

import com.paprika.utils.telegram.dto.ApiCall
import kotlinx.serialization.Serializable

class DeleteWebhook(body: Body): ApiCall("deleteWebhook", body) {
    @Serializable
    data class Body(
        val drop_pending_updates: Boolean = false
    )
}