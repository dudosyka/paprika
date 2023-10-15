package com.paprika.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class UnauthorizedException(@Transient override val msg: String = ""): ClientException(401, "Unauthorized", msg)