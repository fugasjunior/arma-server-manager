package cz.forgottenempire.servermanager.steamauth

data class AuthVerificationResult(
    val status: AuthStatus? = null,
    val message: String? = null,
    val authType: AuthType? = null
) {
    enum class AuthStatus {
        SUCCESS, REQUIRES_2FA, INVALID_CREDENTIALS, ERROR
    }

    enum class AuthType {
        NONE, EMAIL, MOBILE, UNKNOWN
    }

    class Builder {
        private var status: AuthStatus? = null
        private var message: String? = null
        private var authType: AuthType? = null

        fun status(status: AuthStatus) = apply { this.status = status }
        fun message(message: String?) = apply { this.message = message }
        fun authType(authType: AuthType) = apply { this.authType = authType }
        fun build() = AuthVerificationResult(status, message, authType)
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
