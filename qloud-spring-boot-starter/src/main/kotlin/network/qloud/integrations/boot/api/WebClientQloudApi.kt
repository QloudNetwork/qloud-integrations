package network.qloud.integrations.boot.api

import network.qloud.integrations.boot.api.dto.QloudApiUser
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.netty.http.client.HttpClient
import java.util.concurrent.CompletableFuture

class WebClientQloudApi(builder: WebClient.Builder, baseUrl: String, secret: String) : QloudApi {
    private val webClient: WebClient = builder.baseUrl(baseUrl)
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient.create()
                    .compress(true)
                    // follow redirects, so we also support qloud.space subdomains if the application uses a custom domain
                    .followRedirect(true)
            )
        )
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
