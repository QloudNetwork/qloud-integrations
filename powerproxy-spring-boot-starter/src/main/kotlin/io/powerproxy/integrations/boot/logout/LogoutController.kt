package io.powerproxy.integrations.boot.logout

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.UriComponentsBuilder

@Controller
@RequestMapping("\${powerproxy.logoutPath}")
class LogoutController {
    private companion object {
        const val POWERPROXY_LOGOUT_PATH = "logout"
        const val POWERPROXY_RETURN_PATH_PARAM = "return_path"
    }
    @GetMapping
    fun logout(
        authentication: JwtAuthenticationToken,
        @RequestParam("return_path") returnPath: String?
    ): String {
        val issuerUri = authentication.token.issuer.toExternalForm()
        val logoutUrl = buildLogoutUrl(issuerUri, returnPath)
        return "redirect:$logoutUrl"
    }

    private fun buildLogoutUrl(issuerUri: String, returnPath: String?): String =
            UriComponentsBuilder.fromUriString(issuerUri)
                    .pathSegment(POWERPROXY_LOGOUT_PATH).also { builder ->
                        if (!returnPath.isNullOrBlank()) {
                            builder.queryParam(POWERPROXY_RETURN_PATH_PARAM, returnPath)
                        }
                    }
                    .toUriString()
}
