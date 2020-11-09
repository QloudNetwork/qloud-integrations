package io.powerproxy.integrations.boot

import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty("powerproxy.secret")
@AutoConfigureBefore(OAuth2ResourceServerAutoConfiguration::class)
@EnableConfigurationProperties(PowerProxyProperties::class)
class PowerProxyAutoConfiguration(powerProxyProperties: PowerProxyProperties) {
    private val secretKey: SecretKey = SecretKeySpec(powerProxyProperties.secret.toByteArray(), "AES")

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(HS256).build()
    }

    @Bean
    fun bearerTokenResolver(): BearerTokenResolver {
        return PowerProxyTokenResolver()
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
data class PowerProxyProperties(val secret: String)
