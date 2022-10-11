package network.qloud.integrations.boot

import network.qloud.integrations.boot.api.QloudApi
import network.qloud.integrations.boot.api.WebClientQloudApi
import network.qloud.integrations.boot.logout.LogoutController
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtTimestampValidator
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import reactor.netty.http.client.HttpClient
import java.time.Duration
import javax.crypto.spec.SecretKeySpec

@AutoConfiguration(
    before = [OAuth2ResourceServerAutoConfiguration::class],
    after = [WebClientAutoConfiguration::class]
)
@ConditionalOnProperty("qloud.secret")
@EnableConfigurationProperties(QloudProperties::class)
class QloudAutoConfiguration(private val properties: QloudProperties) {
    @Bean
    fun jwtDecoder(): JwtDecoder =
        NimbusJwtDecoder.withSecretKey(SecretKeySpec(properties.secret.toByteArray(), "AES"))
            .macAlgorithm(HS256)
            .build().apply {
                setJwtValidator(JwtTimestampValidator(Duration.ofSeconds(properties.clockSkewSeconds)))
            }

    @Bean
    fun bearerTokenResolver(): BearerTokenResolver = QloudTokenResolver()

    @Bean
    @ConditionalOnExpression("not '\${qloud.logout-path:}'.isBlank()")
    fun logoutController(): LogoutController = LogoutController()

    @Bean
    @Lazy
    @ConditionalOnMissingBean(name = ["qloudApiHttpConnector"])
    fun qloudApiHttpConnector(): ClientHttpConnector = ReactorClientHttpConnector(
        HttpClient.create()
            .compress(true)
            // follow redirects, so we also support qloud.space subdomains if the application uses a custom domain
            .followRedirect(true)
    )

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("not '\${qloud.domain:}'.isBlank()")
    fun qloudApi(connector: ClientHttpConnector): QloudApi =
        WebClientQloudApi(connector, "https://${properties.domain}/.q/api/management", properties.secret)

    @Configuration
    class QloudWebMvcConfiguration : WebMvcConfigurer {
        override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
            resolvers.add(QloudUserArgumentResolver())
        }
    }

    @Configuration
    @ConditionalOnMissingBean(SecurityFilterChain::class, WebSecurityConfigurerAdapter::class)
    class QloudSecurityConfiguration {
        @Bean
        fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
            return http.oauth2ResourceServer { configurer -> configurer.jwt() }
                .authorizeRequests { authorize ->
                    authorize.anyRequest().authenticated().and()
                        .sessionManagement().sessionCreationPolicy(STATELESS)
                }.build()
        }
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "qloud")
data class QloudProperties(
    /**
     * Application domain (e.g. my-app.qloud.space)
     */
    val domain: String?,
    /**
     * Secret used to verify JWT signature.
     */
    val secret: String,
    /**
     * Optional path to the endpoint that redirects to Qloud's logout endpoint.
     *
     * No endpoint is registered if this property is undefined or blank.
     */
    val logoutPath: String?,
    /**
     * Offset in number of seconds to tolerate when validating JWT timestamps.
     *
     * Useful if the clock of the application server is not in sync with the clock of Qloud's authentication server.
     */
    @DefaultValue("30") val clockSkewSeconds: Long
)
