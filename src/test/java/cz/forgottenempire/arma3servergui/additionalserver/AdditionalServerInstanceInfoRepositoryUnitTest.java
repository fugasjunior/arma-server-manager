package cz.forgottenempire.arma3servergui.additionalserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdditionalServerInstanceInfoRepositoryUnitTest {

    private final AdditionalServerInstanceInfoRepository repository;

    public AdditionalServerInstanceInfoRepositoryUnitTest() {
        repository = new AdditionalServerInstanceInfoRepository();
    }

    @Test
    void whenStoreNewServerInstanceInfo_thenServerInstanceInfoCanBeRetrieved() {
        AdditionalServerInstanceInfo instanceInfo = createInstanceInfo(1L);

        repository.storeServerInstanceInfo(1L, instanceInfo);

        AdditionalServerInstanceInfo storedInstanceInfo = repository.getServerInstanceInfo(1L);
        assertThat(storedInstanceInfo).isEqualTo(instanceInfo);
    }

    @Test
    void whenGetNonSavedServersInstanceInfo_thenNewEmptyInstanceStatusIsReturned() {
        AdditionalServerInstanceInfo storedInstanceInfo = repository.getServerInstanceInfo(1L);

        AdditionalServerInstanceInfo expectedInfo = new AdditionalServerInstanceInfo(1L, false, null, null);
        assertThat(storedInstanceInfo).isEqualTo(expectedInfo);
    }

    @Test
    void whenGetAllWithServerInfoStored_thenListOfServerInfoReturned() {
        AdditionalServerInstanceInfo instanceInfo1 = createInstanceInfo(1L);
        AdditionalServerInstanceInfo instanceInfo2 = createInstanceInfo(2L);
        repository.storeServerInstanceInfo(1L, instanceInfo1);
        repository.storeServerInstanceInfo(2L, instanceInfo2);

        List<AdditionalServerInstanceInfo> all = repository.getAll();

        assertThat(all).isNotNull();
        assertThat(all).hasSize(2);
        assertThat(all).containsAll(List.of(instanceInfo1, instanceInfo2));
    }

    @Test
    void whenGetAllWithNoServerInfoStored_thenEmptyListReturned() {
        List<AdditionalServerInstanceInfo> all = repository.getAll();

        assertThat(all).isNotNull();
        assertThat(all).isEmpty();
    }

    private AdditionalServerInstanceInfo createInstanceInfo(Long id) {
        return new AdditionalServerInstanceInfo(id, true, LocalDateTime.now(), null);
    }
}