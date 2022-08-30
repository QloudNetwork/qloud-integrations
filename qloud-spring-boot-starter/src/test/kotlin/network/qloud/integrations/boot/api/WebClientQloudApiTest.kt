package network.qloud.integrations.boot.api

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.Body
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import network.qloud.integrations.boot.api.dto.qloudApiUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.client.reactive.ReactorClientHttpConnector

@WireMockTest
class WebClientQloudApiTest {
    private companion object {
        const val USER_ID = "user-id"
        const val SECRET = "secretsecretsecretsecretsecretse"
        val USER = qloudApiUser()
    }

    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    private val connector = ReactorClientHttpConnector()

    @Test
    fun `getUser returns user from Authenticator Management API`(wireMockInfo: WireMockRuntimeInfo) {
        val api = WebClientQloudApi(connector, wireMockInfo.httpBaseUrl, SECRET)
        stubFor(
            get(userUrl(USER_ID)).willReturn(
                ok().withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withResponseBody(Body(objectMapper.writeValueAsString(USER)))
            )
        )

        val returnedUser = api.getUser(USER_ID).get()

        assertThat(returnedUser).isEqualTo(USER)
    }

    @Test
    fun `deleteUser calls DELETE user endpoint on Authenticator Management API`(wireMockInfo: WireMockRuntimeInfo) {
        val api = WebClientQloudApi(connector, wireMockInfo.httpBaseUrl, SECRET)
        stubFor(delete(userUrl(USER_ID)).willReturn(noContent()))

        api.deleteUser(USER_ID).get()

        verify(deleteRequestedFor(urlEqualTo(userUrl(USER_ID))).withHeader(AUTHORIZATION, equalTo(SECRET)))
    }

    @Suppress("SameParameterValue")
    private fun userUrl(userId: String) = "/users/$userId"
}
