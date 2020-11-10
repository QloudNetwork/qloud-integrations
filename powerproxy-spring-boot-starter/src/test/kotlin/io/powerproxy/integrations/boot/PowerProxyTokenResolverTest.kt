package io.powerproxy.integrations.boot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.mock.web.MockHttpServletRequest
import javax.servlet.http.Cookie

class PowerProxyTokenResolverTest {
    private companion object {
        const val TOKEN = "token"
    }

    private val tokenResolver = PowerProxyTokenResolver()

    @ParameterizedTest
    @ValueSource(strings = [
        "__pp__token__",
        "__Pp__Token__",
        "__PP__TOKEN__"
    ])
    fun `resolve returns token from powerproxy cookie ignoring case`(cookieName: String) {
        val request = MockHttpServletRequest().apply {
            setCookies(Cookie(cookieName, TOKEN))
        }

        val returnedToken = tokenResolver.resolve(request)

        assertThat(returnedToken).isEqualTo(TOKEN)
    }

    @Test
    fun `resolve returns null if request is null`() {
        assertThat(tokenResolver.resolve(request = null)).isNull()
    }

    @Test
    fun `resolve returns null if there are no request cookies`() {
        val request = MockHttpServletRequest().apply {
            setCookies()
        }

        assertThat(tokenResolver.resolve(request)).isNull()
    }

    @Test
    fun `resolve returns null if there is no powerproxy token cookie`() {
        val request = MockHttpServletRequest().apply {
            setCookies(Cookie("some-cookie", "some cookie value"))
        }

        assertThat(tokenResolver.resolve(request)).isNull()
    }
}
