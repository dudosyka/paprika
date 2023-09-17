package com.paprika.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class BadRequestException(@Transient override val msg: String = ""): ClientException(400, "Bad request", msg)