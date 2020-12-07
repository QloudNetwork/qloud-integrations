package io.powerproxy.integrations.boot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver

class PowerProxyAutoConfigurationTest {
    private val contextRunner = WebApplicationContextRunner().withConfiguration(
        AutoConfigurations.of(
            PowerProxyAutoConfiguration::class.java,
            AuthenticationConfiguration::class.java
        )
    )

    @Test
    fun `does not create any beans if powerproxy-secret is not set`() {
        contextRunner.run { context ->
            assertThat(context).doesNotHaveBean(PowerProxyAutoConfiguration::class.java)
        }
    }

    @Test
    fun `creates powerproxy auto configuration if powerproxy-secret is set`() {
        contextRunner.withPropertyValues("powerproxy.secret=secret").run { context ->
            assertThat(context)
                .hasSingleBean(PowerProxyAutoConfiguration::class.java)
                .hasSingleBean(JwtDecoder::class.java)
                .hasSingleBean(BearerTokenResolver::class.java)
        }
    }
}
