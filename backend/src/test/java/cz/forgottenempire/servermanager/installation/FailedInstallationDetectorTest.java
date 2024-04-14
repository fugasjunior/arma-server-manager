package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FailedInstallationDetectorTest {

    @Test
    void setErrorStatusOnInterruptedInstallations_whenInstallationsWereInterrupted_thenStatusesAreSet() {
        ServerInstallation interruptedInstallation = new ServerInstallation();
        interruptedInstallation.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);
        ServerInstallationRepository repository = Mockito.mock(ServerInstallationRepository.class);
        when(repository.findAllByInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS))
                .thenReturn(List.of(interruptedInstallation));

        new FailedInstallationDetector(repository).setErrorStatusOnInterruptedInstallations();

        assertThat(interruptedInstallation.getInstallationStatus())
                .as("The installation status should be 'ERROR'")
                .isEqualTo(InstallationStatus.ERROR);
        assertThat(interruptedInstallation.getErrorStatus())
                .as("The error status should be 'INTERRUPTED'")
                .isEqualTo(ErrorStatus.INTERRUPTED);
        verify(repository).saveAll(List.of(interruptedInstallation));
    }
}
