package network.qloud.integrations.boot.api.dto

import java.time.Instant
import java.time.temporal.ChronoUnit.MICROS
import java.util.UUID.randomUUID

data class QloudApiUser(
    val id: String,
    val provider: String,
    val providerSubject: String,
    val name: String,
    val email: String?,
    val emailVerified: Boolean,
    val signedUpAt: Instant,
)

internal fun qloudApiUser(
    id: String = randomUUID().toString(),
    provider: String = "EMAIL",
    providerSubject: String = "$id@example.com",
    name: String = "Ada Lovelace",
    email: String = "$id@example.com",
    emailVerified: Boolean = true,
    signedUpAt: Instant = Instant.now().truncatedTo(MICROS),
): QloudApiUser = QloudApiUser(
    id = id,
    provider = provider,
    providerSubject = providerSubject,
    name = name,
    email = email,
    emailVerified = emailVerified,
    signedUpAt = signedUpAt,
)
