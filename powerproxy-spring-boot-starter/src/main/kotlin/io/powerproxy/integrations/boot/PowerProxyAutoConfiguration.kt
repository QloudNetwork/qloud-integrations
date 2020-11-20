package io.powerproxy.integrations.boot

import io.powerproxy.integrations.boot.logout.LogoutController
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
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty("powerproxy.secret")
@AutoConfigureBefore(OAuth2ResourceServerAutoConfiguration::class)
@EnableConfigurationProperties(PowerProxyProperties::class)
class PowerProxyAutoConfiguration(powerProxyProperties: PowerProxyProperties) {
    private val secretKey: SecretKey = SecretKeySpec(powerProxyProperties.secret.toByteArray(), "AES")
    private val clockSkewSeconds: Long = powerProxyProperties.clockSkewSeconds

    @Bean
    fun jwtDecoder(): JwtDecoder =
            NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(HS256).build().apply {
                setJwtValidator(JwtTimestampValidator(Duration.ofSeconds(clockSkewSeconds)))
            }

    @Bean
    fun bearerTokenResolver(): BearerTokenResolver = PowerProxyTokenResolver()

    @Bean
    @ConditionalOnExpression("not '\${powerproxy.logout-path:}'.isBlank()")
    fun logoutController(): LogoutController = LogoutController()

    @Configuration
    class PowerProxyWebMvcConfiguration : WebMvcConfigurer {
        override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
            resolvers.add(PowerProxyUserArgumentResolver())
        }
    }

    @Configuration
    @ConditionalOnMissingBean(WebSecurityConfigurerAdapter::class)
    class PowerProxySecurityConfiguration : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http.oauth2ResourceServer { configurer -> configurer.jwt() }
                    .authorizeRequests()
                    .anyRequest().authenticated().and()
                    .sessionManagement().sessionCreationPolicy(STATELESS)
        }
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "powerproxy")
data class PowerProxyProperties(
    /**
     * Secret used to verify JWT token signature.
     */
    val secret: String,
    /**
     * Optional path to the endpoint that redirects to PowerProxy's logout endpoint.
     *
     * No endpoint is registered if this property is undefined or blank.
     */
    val logoutPath: String?,
    /**
     * Offset in number of seconds to tolerate when validating JWT token timestampts.
     *
     * Useful if the clock of the application server is not in sync with the clock of PowerProxy's authentication server.
     */
    @DefaultValue("30") val clockSkewSeconds: Long
)
