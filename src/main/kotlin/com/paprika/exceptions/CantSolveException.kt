package com.paprika.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class CantSolveException(
    @Transient override val msg: String =  "Acceptable menu is not found"
): ClientException(400, "Bad request",msg)