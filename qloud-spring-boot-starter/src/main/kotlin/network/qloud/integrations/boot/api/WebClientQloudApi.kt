package network.qloud.integrations.boot.api

import network.qloud.integrations.boot.api.dto.QloudApiUser
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.util.concurrent.CompletableFuture

class WebClientQloudApi(clientConnector: ClientHttpConnector, baseUrl: String, secret: String) : QloudApi {
    private val webClient: WebClient = WebClient.builder().baseUrl(baseUrl)
        .clientConnector(clientConnector)
        .defaultHeader(AUTHORIZATION, secret)
        .build()

    override fun getUser(id: String): CompletableFuture<QloudApiUser> {
        return webClient.get().uri(userUri(id)).retrieve()
            .bodyToMono(QloudApiUser::class.java)
            .toFuture()
    }

    override fun deleteUser(id: String): CompletableFuture<Void> {
        return webClient.delete().uri(userUri(id)).retrieve()
            .bodyToMono<Void>()
            .toFuture()
    }

    private fun userUri(id: String) = "/users/$id"
}
