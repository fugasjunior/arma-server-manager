package cz.forgottenempire.servermanager.workshop

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppSettingsRepository : JpaRepository<AppSettings, Long>
