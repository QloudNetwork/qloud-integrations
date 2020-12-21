package io.powerproxy.integrations.boot

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer

class PowerProxyUserArgumentResolverTest {
    private companion object {
        val JWT_AUTHENTICATION_TOKEN = jwtAuthenticationToken()
        val ANY_PRINCIPAL = Any()
        val ANY_CREDENTIALS = Any()
        val ANY_PARAMETER = mockk<MethodParameter>()
        val ANY_MAV_CONTAINER = mockk<ModelAndViewContainer>()
        val ANY_WEB_REQUEST = mockk<NativeWebRequest>()
        val ANY_BINDER_FACTORY = mockk<WebDataBinderFactory>()
    }

    private val resolver = PowerProxyUserArgumentResolver()

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `resolveArgument returns PowerProxyUser if authentication is JwtAuthenticationToken`() {
        with(SecurityContextHolder.getContext()) {
            authentication = JWT_AUTHENTICATION_TOKEN
        }

        val user = resolver.resolveArgument(ANY_PARAMETER, ANY_MAV_CONTAINER, ANY_WEB_REQUEST, ANY_BINDER_FACTORY)

        assertThat(user).isEqualToComparingFieldByField(JwtUser(JWT_AUTHENTICATION_TOKEN))
    }

    @Test
    fun `resolveArgument returns null if authentication is not JwtAuthenticationToken`() {
        with(SecurityContextHolder.getContext()) {
            authentication = UsernamePasswordAuthenticationToken(ANY_PRINCIPAL, ANY_CREDENTIALS)
        }

        resolver.resolveArgument(ANY_PARAMETER, ANY_MAV_CONTAINER, ANY_WEB_REQUEST, ANY_BINDER_FACTORY)
    }
}
