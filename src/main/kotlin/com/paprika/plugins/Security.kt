package com.paprika.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.paprika.conf.AppConf
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*


fun Application.configureSecurity() {

    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(AppConf.jwt.secret))
        .withIssuer(AppConf.jwt.domain)
        .build()

    authentication {
        jwt("authorized") {
            verifier(jwtVerifier)
            validate {
                JWTPrincipal(it.payload)
            }
        }
    }
}

fun createToken(claims: MutableMap<String, String>): String {
    return JWT.create()
        .withIssuer(AppConf.jwt.domain)
        .withExpiresAt(Date(System.currentTimeMillis() + AppConf.jwt.expiration))
        .apply {
            claims.forEach {
                withClaim(it.key, it.value)
            }
        }.sign(Algorithm.HMAC256(AppConf.jwt.secret))
}