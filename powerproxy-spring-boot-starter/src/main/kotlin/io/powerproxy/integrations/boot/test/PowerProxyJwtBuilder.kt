package io.powerproxy.integrations.boot.test

import org.springframework.security.oauth2.jwt.Jwt
import java.util.UUID

/**
 * To be used to build PowerProxy JWTs in Spring MVC integration tests.
 *
 * See https://docs.spring.io/spring-security/site/docs/current/reference/html5/#testing-jwt
 */
class PowerProxyJwtBuilder(
    private var subject: String = UUID.randomUUID().toString(),
    private var name: String = "Max Power",
    private var email: String? = "max.power@powerproxy.io",
    private var identityProvider: String = "google"
) {
    fun subject(subject: String): PowerProxyJwtBuilder {
        this.subject = subject
        return this
    }

    fun name(name: String): PowerProxyJwtBuilder {
        this.name = name
        return this
    }

    fun email(email: String?): PowerProxyJwtBuilder {
        this.email = email
        return this
    }

    fun identityProvider(identityProvider: String): PowerProxyJwtBuilder {
        this.identityProvider = identityProvider
        return this
    }

    fun build(): Jwt = Jwt.withTokenValue("token")
            .header("alg", "HS256")
            .subject(subject)
            .claim("name", name)
            .claim("email", email)
            .claim("pp:via", identityProvider)
            .build()
}
