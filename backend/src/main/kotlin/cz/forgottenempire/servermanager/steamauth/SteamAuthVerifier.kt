package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.SteamAuthDto

interface SteamAuthVerifier {
    fun verifyCredentials(authDto: SteamAuthDto): AuthVerificationResult
}
