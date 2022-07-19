package network.qloud.integrations.boot.test

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test

class QloudJwtBuilderTest {
    private companion object {
        const val SUBJECT = "subject"
        const val NAME = "Sepp Forcher"
        const val EMAIL = "sepp.forcher@qloud.network"
        const val IDENTITY_PROVIDER = "google"
        const val IDENTITY_PROVIDER_SUBJECT = "123"
        const val USER_DATABASE = "user-database"
    }

    @Test
    fun `build returns some Qloud JWT`() {
        val jwt = QloudJwtBuilder().build()

        assertSoftly { softly ->
            softly.assertThat(jwt.headers["alg"]).isEqualTo("HS256")
            softly.assertThat(jwt.subject).isNotBlank
            softly.assertThat(jwt.claims["name"]).isNotNull
            softly.assertThat(jwt.claims["email"]).isNotNull
            softly.assertThat(jwt.claims["pp:idp"]).isNotNull
            softly.assertThat(jwt.claims["pp:idp-sub"]).isNotNull
            softly.assertThat(jwt.claims["pp:udb"]).isNotNull
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
            softly.assertThat(jwt.claims["pp:idp"]).isEqualTo(IDENTITY_PROVIDER)
            softly.assertThat(jwt.claims["pp:idp-sub"]).isEqualTo(IDENTITY_PROVIDER_SUBJECT)
            softly.assertThat(jwt.claims["pp:udb"]).isEqualTo(USER_DATABASE)
        }
    }
}
