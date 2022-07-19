package network.qloud.integrations.boot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver

class QloudAutoConfigurationTest {
    private val contextRunner = WebApplicationContextRunner().withConfiguration(
        AutoConfigurations.of(
            SecurityAutoConfiguration::class.java,
            QloudAutoConfiguration::class.java,
            AuthenticationConfiguration::class.java
        )
    )

    @Test
    fun `does not create any beans if qloud-secret is not set`() {
        contextRunner.run { context ->
            assertThat(context).doesNotHaveBean(QloudAutoConfiguration::class.java)
        }
    }

    @Test
    fun `creates qloud auto configuration if qloud-secret is set`() {
        contextRunner.withPropertyValues("qloud.secret=secret").run { context ->
            assertThat(context)
                .hasSingleBean(QloudAutoConfiguration::class.java)
                .hasSingleBean(JwtDecoder::class.java)
                .hasSingleBean(BearerTokenResolver::class.java)
        }
    }
}
