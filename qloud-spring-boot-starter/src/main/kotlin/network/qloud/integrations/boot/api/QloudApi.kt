package network.qloud.integrations.boot.api

import network.qloud.integrations.boot.api.dto.QloudApiUser
import java.util.concurrent.CompletableFuture

interface QloudApi {
    fun getUser(id: String): CompletableFuture<QloudApiUser>
    fun deleteUser(id: String): CompletableFuture<Void>
}
