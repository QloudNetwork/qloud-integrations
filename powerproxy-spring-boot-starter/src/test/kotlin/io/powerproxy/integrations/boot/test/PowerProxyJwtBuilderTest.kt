package io.powerproxy.integrations.boot.test

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test

class PowerProxyJwtBuilderTest {
    private companion object {
        const val SUBJECT = "subject"
        const val NAME = "Sepp Forcher"
        const val EMAIL = "sepp.forcher@powerproxy.io"
        const val IDENTITY_PROVIDER = "google"
    }

    @Test
    fun `build returns some PowerProxy JWT`() {
        val jwt = PowerProxyJwtBuilder().build()

        assertSoftly { softly ->
            softly.assertThat(jwt.headers["alg"]).isEqualTo("HS256")
            softly.assertThat(jwt.subject).isNotBlank
            softly.assertThat(jwt.claims["name"]).isNotNull
            softly.assertThat(jwt.claims["email"]).isNotNull
            softly.assertThat(jwt.claims["pp:via"]).isNotNull
        }
    }

    @Test
    fun `build returns JWT with provided claims`() {
        val jwt = PowerProxyJwtBuilder()
                .subject(SUBJECT)
                .name(NAME)
                .email(EMAIL)
                .identityProvider(IDENTITY_PROVIDER)
                .build()

        assertSoftly { softly ->
            softly.assertThat(jwt.subject).isEqualTo(SUBJECT)
            softly.assertThat(jwt.claims["name"]).isEqualTo(NAME)
            softly.assertThat(jwt.claims["email"]).isEqualTo(EMAIL)
            softly.assertThat(jwt.claims["pp:via"]).isEqualTo(IDENTITY_PROVIDER)
        }
    }
}
