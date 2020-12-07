package io.powerproxy.integrations.boot

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test

class JwtPowerProxyUserTest {
    private companion object {
        const val SUBJECT = "subject"
        const val NAME = "name"
        const val EMAIL = "email@domain.com"
        const val IDENTITY_PROVIDER = "google"
    }

    @Test
    fun `constructor populates fields from JwtAuthenticationToken`() {
        val token = jwtAuthenticationToken(
            claims = mapOf(
                "sub" to SUBJECT,
                "name" to NAME,
                "email" to EMAIL,
                "pp:idp" to IDENTITY_PROVIDER
            )
        )

        val user = JwtPowerProxyUser(token)

        assertSoftly { softly ->
            softly.assertThat(user.subject).isEqualTo(SUBJECT)
            softly.assertThat(user.name).isEqualTo(NAME)
            softly.assertThat(user.email).isEqualTo(EMAIL)
            softly.assertThat(user.identityProvider).isEqualTo(IDENTITY_PROVIDER)
        }
    }
}
