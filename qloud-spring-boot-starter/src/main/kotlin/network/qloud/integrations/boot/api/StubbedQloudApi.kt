package network.qloud.integrations.boot.api

import network.qloud.integrations.boot.api.dto.QloudApiUser
import network.qloud.integrations.boot.api.dto.qloudApiUser
import java.util.concurrent.CompletableFuture

class StubbedQloudApi : QloudApi {
    override fun getUser(id: String): CompletableFuture<QloudApiUser> {
        return CompletableFuture.completedFuture(qloudApiUser(id = id))
    }

    override fun deleteUser(id: String): CompletableFuture<Void> {
        return CompletableFuture.completedFuture(null)
    }
}
