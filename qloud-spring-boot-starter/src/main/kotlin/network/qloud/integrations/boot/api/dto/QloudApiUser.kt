package network.qloud.integrations.boot.api.dto

import java.time.Instant

data class QloudApiUser(
    val id: String,
    val provider: String,
    val providerSubject: String,
    val name: String,
    val email: String?,
    val emailVerified: Boolean,
    val signedUpAt: Instant,
)
