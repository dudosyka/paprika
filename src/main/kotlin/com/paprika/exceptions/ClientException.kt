package com.paprika.exceptions

import kotlinx.serialization.Serializable

@Serializable
open class ClientException(
    open val status: Int,
    open val statusDescription: String,
    open val msg: String
): Exception(msg)