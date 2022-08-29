package network.qloud.integrations.boot.api.dto

import java.time.Instant
import java.time.temporal.ChronoUnit.MICROS
import java.util.UUID.randomUUID

fun qloudApiUser(): QloudApiUser = QloudApiUser(
    id = randomUUID().toString(),
    provider = "GOOGLE",
    providerSubject = "117252491205132245210",
    name = "Max Power",
    email = "max.power@example.com",
    emailVerified = true,
    signedUpAt = Instant.now().truncatedTo(MICROS),
)
