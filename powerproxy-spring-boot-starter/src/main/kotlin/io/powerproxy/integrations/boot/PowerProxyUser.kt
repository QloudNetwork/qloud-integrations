package io.powerproxy.integrations.boot

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

interface PowerProxyUser {
    val subject: String
    val name: String
    val email: String?
    val identityProvider: String
}

internal class JwtPowerProxyUser(authentication: JwtAuthenticationToken) : PowerProxyUser {
    override val subject = authentication.token.claims["sub"] as String
    override val name = authentication.token.claims["name"] as String
    override val email = authentication.token.claims["email"] as String?
    override val identityProvider = authentication.token.claims["pp:via"] as String
}
