package com.paprika.exceptions

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
class CantSolveException(
): ClientException(400, "Bad request", "Acceptable menu is not found") {}