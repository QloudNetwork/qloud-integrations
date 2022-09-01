package network.qloud.integrations.boot.logout

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.UriComponentsBuilder

@Controller
@RequestMapping("\${qloud.logout-path}")
class LogoutController {
    private companion object {
        const val QLOUD_LOGOUT_PATH = "logout"
        const val QLOUD_RETURN_PATH_PARAM = "return_path"
    }

    @RequestMapping(method = [GET, POST])
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
            .pathSegment(QLOUD_LOGOUT_PATH).also { builder ->
                if (!returnPath.isNullOrBlank()) {
                    builder.queryParam(QLOUD_RETURN_PATH_PARAM, returnPath)
                }
            }
            .toUriString()
}
