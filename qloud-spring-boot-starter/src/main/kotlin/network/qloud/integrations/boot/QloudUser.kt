package network.qloud.integrations.boot

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

interface QloudUser {
    val subject: String
    val name: String
    val email: String?
    val identityProvider: String
    val identityProviderSubject: String
    val userDatabase: String
}

internal class JwtQloudUser(authentication: JwtAuthenticationToken) : QloudUser {
    override val subject = authentication.token.claims["sub"] as String
    override val name = authentication.token.claims["name"] as String
    override val email = authentication.token.claims["email"] as String?
    override val identityProvider = authentication.token.claims["q:idp"] as String
    override val identityProviderSubject: String = authentication.token.claims["q:idp-sub"] as String
    override val userDatabase: String = authentication.token.claims["q:udb"] as String
}
