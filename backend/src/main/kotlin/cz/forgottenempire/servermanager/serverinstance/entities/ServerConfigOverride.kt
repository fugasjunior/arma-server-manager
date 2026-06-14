package cz.forgottenempire.servermanager.serverinstance.entities

import cz.forgottenempire.servermanager.serverinstance.ConfigFileKey
import jakarta.persistence.*
import java.util.Objects

@Entity
@Table(
    name = "server_config_override",
    uniqueConstraints = [UniqueConstraint(columnNames = ["server_id", "config_key"])]
)
class ServerConfigOverride(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "server_id", nullable = false)
    var serverId: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "config_key", nullable = false, length = 64)
    var configKey: ConfigFileKey? = null,

    @Column(columnDefinition = "LONGTEXT")
    var content: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServerConfigOverride) return false
        return id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id)
}
