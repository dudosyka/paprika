package com.paprika.utils.telegram.dto

import com.paprika.utils.serializer.AnyTypeSerializer
import kotlinx.serialization.Serializable

@Serializable
open class ApiResult (
    val ok: Boolean,
    val result: Map<String, @Serializable(with = AnyTypeSerializer::class) Any?>
)