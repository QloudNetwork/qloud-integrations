package network.qloud.integrations.boot

import network.qloud.integrations.boot.api.QloudApi
import network.qloud.integrations.boot.api.WebClientQloudApi
import network.qloud.integrations.boot.logout.LogoutController
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration
import javax.crypto.spec.SecretKeySpec

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty("qloud.secret")
@AutoConfigureBefore(OAuth2ResourceServerAutoConfiguration::class)
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
    @ConditionalOnExpression("not '\${qloud.domain:}'.isBlank()")
    fun qloudApi(builder: WebClient.Builder): QloudApi =
        WebClientQloudApi(builder, "https://${properties.domain}/.q/api/management", properties.secret)

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
     * Secret used to verify JWT token signature.
     */
    val secret: String,
    /**
     * Optional path to the endpoint that redirects to Qloud's logout endpoint.
     *
     * No endpoint is registered if this property is undefined or blank.
     */
    val logoutPath: String?,
    /**
     * Offset in number of seconds to tolerate when validating JWT token timestamps.
     *
     * Useful if the clock of the application server is not in sync with the clock of Qloud's authentication server.
     */
    @DefaultValue("30") val clockSkewSeconds: Long
)
