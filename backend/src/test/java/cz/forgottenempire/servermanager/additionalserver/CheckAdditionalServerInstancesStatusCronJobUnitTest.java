package cz.forgottenempire.servermanager.additionalserver;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CheckAdditionalServerInstancesStatusCronJobUnitTest {

    private final AdditionalServerInstanceInfoRepository instanceInfoRepository;
    private final AdditionalServersService additionalServersService;
    private final CheckAdditionalServerInstancesStatusCronJob cronJob;
    private final Process process;

    public CheckAdditionalServerInstancesStatusCronJobUnitTest() {
        instanceInfoRepository = mock(AdditionalServerInstanceInfoRepository.class);
        additionalServersService = mock(AdditionalServersService.class);
        process = mock(Process.class);

        cronJob = new CheckAdditionalServerInstancesStatusCronJob(instanceInfoRepository, additionalServersService);
    }

    @Test
    void whenServersAreRunningAndProcessIsAlive_thenServerInfoIsNotUpdated() {
        when(process.isAlive()).thenReturn(true);
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, true, LocalDateTime.now(), process);
        when(instanceInfoRepository.getAll()).thenReturn(List.of(instanceInfo));

        cronJob.checkServers();

        verify(process, times(1)).isAlive();
        verify(instanceInfoRepository, times(0)).storeServerInstanceInfo(any(), any());
    }

    @Test
    void whenServerHasCrashed_thenServerInfoIsUpdated() {
        when(process.isAlive()).thenReturn(false);
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, true, LocalDateTime.now(), process);
        AdditionalServer server = new AdditionalServer();
        server.setId(1L);
        when(additionalServersService.getServer(1L)).thenReturn(Optional.of(server));
        when(instanceInfoRepository.getAll()).thenReturn(List.of(instanceInfo));

        cronJob.checkServers();

        AdditionalServerInstanceInfo expectedInstanceInfo
                = new AdditionalServerInstanceInfo(1L, false, null, null);
        verify(process, times(1)).isAlive();
        verify(instanceInfoRepository, times(1))
                .storeServerInstanceInfo(1L, expectedInstanceInfo);
    }

    @Test
    void whenServerHasCrashedAndHasInvalidId_thenIllegalStateExceptionIsThrown() {
        when(process.isAlive()).thenReturn(false);
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, true, LocalDateTime.now(), process);
        when(additionalServersService.getServer(1L)).thenReturn(Optional.empty());
        when(instanceInfoRepository.getAll()).thenReturn(List.of(instanceInfo));

        assertThatThrownBy(cronJob::checkServers)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid ID 1 in additional server instances map");

        AdditionalServerInstanceInfo expectedInstanceInfo
                = new AdditionalServerInstanceInfo(1L, false, null, null);
        verify(process, times(1)).isAlive();
        verify(instanceInfoRepository, times(1))
                .storeServerInstanceInfo(1L, expectedInstanceInfo);
    }

    @Test
    void whenNoServersAreRunning_noServerInfoIsStored() {
        when(instanceInfoRepository.getAll()).thenReturn(Collections.emptyList());

        cronJob.checkServers();

        verify(instanceInfoRepository, times(0)).storeServerInstanceInfo(any(), any());
    }
}