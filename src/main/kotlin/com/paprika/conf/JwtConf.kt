package com.paprika.conf

data class JwtConf (
    val secret: String,
    val domain: String,
    val expiration: Long
)