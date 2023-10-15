package com.paprika.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class ForbiddenException(@Transient override val msg: String = ""): ClientException(403, "Forbidden", msg)