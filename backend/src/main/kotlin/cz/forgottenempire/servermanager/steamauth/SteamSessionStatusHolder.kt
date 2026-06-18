package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.SessionStatus
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class SteamSessionStatusHolder {

    @Volatile
    var status: SessionStatus = SessionStatus.UNKNOWN

    @Volatile
    var lastCheckedAt: OffsetDateTime? = null

    fun setActive() {
        status = SessionStatus.ACTIVE
        lastCheckedAt = OffsetDateTime.now()
    }

    fun setExpired() {
        status = SessionStatus.EXPIRED
        lastCheckedAt = OffsetDateTime.now()
    }

    fun setNotConfigured() {
        status = SessionStatus.NOT_CONFIGURED
        lastCheckedAt = OffsetDateTime.now()
    }
}
