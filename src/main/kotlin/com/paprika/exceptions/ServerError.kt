package com.paprika.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class ServerError(@Transient override val msg: String = ""): ClientException(500, "Internal server error", msg)