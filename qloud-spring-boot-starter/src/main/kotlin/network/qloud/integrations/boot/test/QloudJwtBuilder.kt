package network.qloud.integrations.boot.test

import com.nimbusds.jose.JWSAlgorithm.HS256
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.oauth2.jwt.Jwt
import java.util.*

/**
 * To be used to build Qloud JWTs in Spring MVC integration tests.
 *
 * See https://docs.spring.io/spring-security/site/docs/current/reference/html5/#testing-jwt
 */
class QloudJwtBuilder(
    private var subject: String = UUID.randomUUID().toString(),
    private var name: String = "Max Power",
    private var email: String? = "max.power@qloud.network",
    private var identityProvider: String = "google",
    private var identityProviderSubject: String = "123",
    private var userDatabase: String = UUID.randomUUID().toString()
) {
    private companion object {
        val DEFAULT_SECRET = "qsecretqsecretqsecretqsecretqsec".toByteArray()
    }

    fun subject(subject: String): QloudJwtBuilder {
        this.subject = subject
        return this
    }

    fun name(name: String): QloudJwtBuilder {
        this.name = name
        return this
    }

    fun email(email: String?): QloudJwtBuilder {
        this.email = email
        return this
    }

    fun identityProvider(identityProvider: String): QloudJwtBuilder {
        this.identityProvider = identityProvider
        return this
    }

    fun identityProviderSubject(identityProviderSubject: String): QloudJwtBuilder {
        this.identityProviderSubject = identityProviderSubject
        return this
    }

    fun userDatabase(userDatabase: String): QloudJwtBuilder {
        this.userDatabase = userDatabase
        return this
    }

    fun build(secret: ByteArray = DEFAULT_SECRET): Jwt {
        require(secret.size == 32) { "secret must consist of exactly 32 bytes" }

        val claimSet = JWTClaimsSet.Builder()
            .subject(subject)
            .claim("name", name)
            .claim("email", email)
            .claim("q:idp", identityProvider)
            .claim("q:idp-sub", identityProviderSubject)
            .claim("q:udb", userDatabase)
            .build()
        val signedJwt = SignedJWT(JWSHeader(HS256), claimSet).apply { sign(MACSigner(secret)) }
        return Jwt.withTokenValue(signedJwt.serialize())
            .header("alg", "HS256")
            .claims { claims -> claims.putAll(claimSet.claims) }
            .build()
    }
}
