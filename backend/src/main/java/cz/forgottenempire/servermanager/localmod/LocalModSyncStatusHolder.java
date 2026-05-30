package cz.forgottenempire.servermanager.localmod;

import org.springframework.stereotype.Component;

@Component
public class LocalModSyncStatusHolder {

    private volatile LocalModSyncStatus status = LocalModSyncStatus.IDLE;

    public LocalModSyncStatus getStatus() {
        return status;
    }

    public void setStatus(LocalModSyncStatus newStatus) {
        this.status = newStatus;
    }
}
