package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.common.AttributeEncryptor
import jakarta.persistence.Convert
import java.util.Objects
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class SteamAuth(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var username: String? = null,
    @Convert(converter = AttributeEncryptor::class)
    var password: String? = null,
    var steamGuardToken: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SteamAuth) return false
        return username == other.username &&
            password == other.password &&
            steamGuardToken == other.steamGuardToken
    }

    override fun hashCode(): Int = Objects.hash(username, password, steamGuardToken)
}
