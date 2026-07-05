package cz.forgottenempire.servermanager.workshop

import cz.forgottenempire.servermanager.common.InstallationStatus
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadata
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class WorkshopUpdateCheckJobTest {

    @Mock
    private lateinit var modsService: WorkshopModsService

    @Mock(stubOnly = true)
    private lateinit var metadataService: ModMetadataService

    @InjectMocks
    private lateinit var job: WorkshopUpdateCheckJob

    @Test
    fun `when newer workshop version exists, latestWorkshopUpdatedAt is updated`() {
        val installed = LocalDateTime.of(2024, 1, 1, 0, 0)
        val latest = LocalDateTime.of(2024, 6, 1, 0, 0)

        val mod = WorkshopMod(1L).apply {
            installationStatus = InstallationStatus.FINISHED
            installedWorkshopUpdatedAt = installed
            latestWorkshopUpdatedAt = installed
        }
        `when`(modsService.getAllMods()).thenReturn(listOf(mod))
        `when`(metadataService.fetchModMetadata(listOf(1L)))
            .thenReturn(mapOf(1L to ModMetadata("Mod Name", "107410", latest)))
        `when`(modsService.saveMod(mod)).thenReturn(mod)

        job.checkForUpdates()

        val captor = ArgumentCaptor.forClass(WorkshopMod::class.java)
        verify(modsService).saveMod(captor.capture())
        assertThat(captor.value.latestWorkshopUpdatedAt).isEqualTo(latest)
        assertThat(captor.value.installedWorkshopUpdatedAt).isEqualTo(installed)
    }

    @Test
    fun `when mod not in finished state, it is skipped`() {
        val mod = WorkshopMod(2L).apply {
            installationStatus = InstallationStatus.INSTALLATION_IN_PROGRESS
        }
        `when`(modsService.getAllMods()).thenReturn(listOf(mod))

        job.checkForUpdates()

        verify(modsService, never()).saveMod(ArgumentCaptor.forClass(WorkshopMod::class.java).capture())
    }

    @Test
    fun `when steam returns no timeUpdated, mod is not saved`() {
        val mod = WorkshopMod(3L).apply {
            installationStatus = InstallationStatus.FINISHED
        }
        `when`(modsService.getAllMods()).thenReturn(listOf(mod))
        `when`(metadataService.fetchModMetadata(listOf(3L)))
            .thenReturn(mapOf(3L to ModMetadata("Mod Name", "107410", null)))

        job.checkForUpdates()

        verify(modsService, never()).saveMod(ArgumentCaptor.forClass(WorkshopMod::class.java).capture())
    }
}
