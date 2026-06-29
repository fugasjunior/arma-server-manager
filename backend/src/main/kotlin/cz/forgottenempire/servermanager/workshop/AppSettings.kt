package cz.forgottenempire.servermanager.workshop

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalTime
import java.util.Objects

@Entity
class AppSettings(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var automaticModUpdateEnabled: Boolean = true,
    var automaticModUpdateTime: LocalTime = LocalTime.of(3, 0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppSettings) return false
        return automaticModUpdateEnabled == other.automaticModUpdateEnabled &&
            automaticModUpdateTime == other.automaticModUpdateTime
    }

    override fun hashCode(): Int = Objects.hash(automaticModUpdateEnabled, automaticModUpdateTime)
}
