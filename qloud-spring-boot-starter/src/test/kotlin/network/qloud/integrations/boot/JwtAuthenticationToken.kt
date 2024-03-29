package network.qloud.integrations.boot

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.time.Instant

const val ANY_TOKEN_VALUE = "any-token"
val ANY_ISSUED_AT: Instant = Instant.now()
val ANY_EXPIRES_AT: Instant = Instant.now()
val ANY_HEADERS = mapOf("header" to "value")
val CLAIMS = mapOf(
    "sub" to "subject",
    "name" to "name",
    "email" to "email@domain.com",
    "q:idp" to "google",
    "q:idp-sub" to "123456",
    "q:udb" to "0c955b26-0586-49d2-b3f2-8149e6d2c8c4",
)

fun jwtAuthenticationToken(claims: Map<String, Any> = CLAIMS): JwtAuthenticationToken = JwtAuthenticationToken(
    Jwt(
        ANY_TOKEN_VALUE,
        ANY_ISSUED_AT,
        ANY_EXPIRES_AT,
        ANY_HEADERS,
        claims
    )
)
