package io.powerproxy.integrations.boot

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import javax.servlet.http.HttpServletRequest

class PowerProxyTokenResolver : BearerTokenResolver {
    private companion object {
        const val PP_TOKEN_COOKIE = "__pp__token__"
    }

    override fun resolve(request: HttpServletRequest?): String? {
        return request?.cookies?.find { it.name.equals(PP_TOKEN_COOKIE, ignoreCase = true) }?.value
    }
}
