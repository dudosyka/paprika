package com.paprika.services.telegram.updates

import com.paprika.utils.telegram.getTOrNull
import com.paprika.utils.serializer.AnyTypeSerializer
import kotlinx.serialization.Serializable

@Serializable
data class TelegramUpdatesListDto (
    val ok: Boolean,
    val result: List<Map<String, @Serializable(with = AnyTypeSerializer::class) Any?>>
) {

//    infix fun Map<String, @Serializable(with = AnyTypeSerializer::class) Any?>.typeIs(type: String): Boolean = containsKey(type)

    fun getLastUpdateId(): Long? = result.lastOrNull()?.run {
            getTOrNull("update_id")
        }


}