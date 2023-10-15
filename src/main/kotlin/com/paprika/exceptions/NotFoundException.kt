package com.paprika.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class NotFoundException(@Transient override val msg: String = ""): ClientException(404, "Model not found", msg)