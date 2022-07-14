package io.powerproxy.integrations.boot.logout

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class LogoutControllerTest {
    private companion object {
        const val ISSUER_URI = "https://console.powerproxy.io/.q"
        const val RETURN_PATH = "/return-path"
        fun jwtToken(issuerUri: String) = JwtAuthenticationToken(
            Jwt.withTokenValue("any-token")
                .header("alg", "none")
                .issuer(issuerUri)
                .build()
        )
    }

    private val controller = LogoutController()

    @Test
    fun `logout returns redirect to issuer URI with appended logout path`() {
        val jwt = jwtToken(ISSUER_URI)

        val redirect = controller.logout(authentication = jwt, returnPath = null)

        assertThat(redirect).isEqualTo("redirect:$ISSUER_URI/logout")
    }

    @Test
    fun `logout returns redirect to issuer URI with with appended logout path and return_path query parameter`() {
        val jwt = jwtToken(ISSUER_URI)

        val redirect = controller.logout(authentication = jwt, returnPath = RETURN_PATH)

        assertThat(redirect).isEqualTo("redirect:$ISSUER_URI/logout?return_path=$RETURN_PATH")
    }
}
