package network.qloud.integrations.boot

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test

class JwtQloudUserTest {
    private companion object {
        const val SUBJECT = "subject"
        const val NAME = "name"
        const val EMAIL = "email@domain.com"
        const val IDENTITY_PROVIDER = "google"
        const val IDENTITY_PROVIDER_SUBJECT = "123456"
        const val USER_DATABASE = "0c955b26-0586-49d2-b3f2-8149e6d2c8c4"
    }

    @Test
    fun `constructor populates fields from JwtAuthenticationToken`() {
        val token = jwtAuthenticationToken(
            claims = mapOf(
                "sub" to SUBJECT,
                "name" to NAME,
                "email" to EMAIL,
                "q:idp" to IDENTITY_PROVIDER,
                "q:idp-sub" to IDENTITY_PROVIDER_SUBJECT,
                "q:udb" to USER_DATABASE,
            )
        )

        val user = JwtQloudUser(token)

        assertSoftly { softly ->
            softly.assertThat(user.subject).isEqualTo(SUBJECT)
            softly.assertThat(user.name).isEqualTo(NAME)
            softly.assertThat(user.email).isEqualTo(EMAIL)
            softly.assertThat(user.identityProvider).isEqualTo(IDENTITY_PROVIDER)
            softly.assertThat(user.identityProviderSubject).isEqualTo(IDENTITY_PROVIDER_SUBJECT)
            softly.assertThat(user.userDatabase).isEqualTo(USER_DATABASE)
        }
    }
}
