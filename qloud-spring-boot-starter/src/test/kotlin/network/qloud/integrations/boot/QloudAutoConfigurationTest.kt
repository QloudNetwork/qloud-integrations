package network.qloud.integrations.boot

import network.qloud.integrations.boot.api.QloudApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver

class QloudAutoConfigurationTest {
    private val contextRunner = WebApplicationContextRunner().withConfiguration(
        AutoConfigurations.of(
            SecurityAutoConfiguration::class.java,
            WebClientAutoConfiguration::class.java,
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

    @Test
    fun `creates QloudApi bean if qloud-secret and qloud-domain is set`() {
        contextRunner.withPropertyValues("qloud.secret=secret", "qloud.domain=domain").run { context ->
            assertThat(context).hasSingleBean(QloudApi::class.java)
        }
    }

    @Test
    fun `does not create QloudApi bean if qloud-domain is blank`() {
        contextRunner.withPropertyValues("qloud.secret=secret", "qloud.domain=").run { context ->
            assertThat(context).doesNotHaveBean(QloudApi::class.java)
        }
    }

    @Test
    fun `creates ReactClientHttpConnector if no bean named qloudApiHttpConnector exists`() {
        contextRunner.withPropertyValues("qloud.secret=secret").run { context ->
            assertThat(context)
                .getBean("qloudApiHttpConnector")
                .isInstanceOf(ReactorClientHttpConnector::class.java)
        }
    }

    @Test
    fun `allows overriding of qloudApiHttpConnector`() {
        contextRunner
            .withPropertyValues("qloud.secret=secret")
            .withBean("qloudApiHttpConnector", ClientHttpConnector::class.java, { HttpComponentsClientHttpConnector() })
            .run { context ->
                assertThat(context)
                    .getBean("qloudApiHttpConnector")
                    .isInstanceOf(HttpComponentsClientHttpConnector::class.java)
            }
    }
}
