package network.qloud.integrations.boot.test

import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class QloudJwtBuilderTest {
    private companion object {
        const val SUBJECT = "subject"
        const val NAME = "Sepp Forcher"
        const val EMAIL = "sepp.forcher@qloud.network"
        const val IDENTITY_PROVIDER = "google"
        const val IDENTITY_PROVIDER_SUBJECT = "123"
        const val USER_DATABASE = "user-database"
        val SECRET = ByteArray(32)
    }

    @Test
    fun `build returns some Qloud JWT`() {
        val jwt = QloudJwtBuilder().build()

        assertSoftly { softly ->
            softly.assertThat(jwt.headers["alg"]).isEqualTo("HS256")
            softly.assertThat(jwt.subject).isNotBlank
            softly.assertThat(jwt.claims["name"]).isNotNull
            softly.assertThat(jwt.claims["email"]).isNotNull
            softly.assertThat(jwt.claims["q:idp"]).isNotNull
            softly.assertThat(jwt.claims["q:idp-sub"]).isNotNull
            softly.assertThat(jwt.claims["q:udb"]).isNotNull
        }
    }

    @Test
    fun `build returns JWT with provided claims`() {
        val jwt = QloudJwtBuilder()
            .subject(SUBJECT)
            .name(NAME)
            .email(EMAIL)
            .identityProvider(IDENTITY_PROVIDER)
            .identityProviderSubject(IDENTITY_PROVIDER_SUBJECT)
            .userDatabase(USER_DATABASE)
            .build()

        assertSoftly { softly ->
            softly.assertThat(jwt.subject).isEqualTo(SUBJECT)
            softly.assertThat(jwt.claims["name"]).isEqualTo(NAME)
            softly.assertThat(jwt.claims["email"]).isEqualTo(EMAIL)
            softly.assertThat(jwt.claims["q:idp"]).isEqualTo(IDENTITY_PROVIDER)
            softly.assertThat(jwt.claims["q:idp-sub"]).isEqualTo(IDENTITY_PROVIDER_SUBJECT)
            softly.assertThat(jwt.claims["q:udb"]).isEqualTo(USER_DATABASE)
        }
    }

    @Test
    fun `build returns JWT signed with given secret`() {
        val jwt = QloudJwtBuilder().build(secret = SECRET)

        val signedJwt = SignedJWT.parse(jwt.tokenValue)
        assertThat(signedJwt.verify(MACVerifier(SECRET))).isTrue
    }

    @ParameterizedTest
    @ValueSource(ints = [31, 33])
    fun `build throws IllegalArgumentException if given secret does not consist of exactly 32 bytes`(byteSize: Int) {
        val exception = assertThrows<IllegalArgumentException> { QloudJwtBuilder().build(secret = ByteArray(byteSize)) }

        assertThat(exception).isNotNull
        assertThat(exception.message).isEqualTo("secret must consist of exactly 32 bytes")
    }
}
