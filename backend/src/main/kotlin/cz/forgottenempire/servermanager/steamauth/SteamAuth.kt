package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.common.AttributeEncryptor
import jakarta.persistence.Convert
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
)
