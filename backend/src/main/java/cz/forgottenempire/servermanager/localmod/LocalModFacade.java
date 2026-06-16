package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.api.model.ModFlagsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class LocalModFacade {

    private final LocalModService modService;
    private final LocalModInstallerService installerService;
    private final LocalModSyncStatusHolder statusHolder;
    private final AtomicBoolean syncRunning = new AtomicBoolean(false);

    @Autowired
    public LocalModFacade(
            LocalModService modService,
            LocalModInstallerService installerService,
            LocalModSyncStatusHolder statusHolder) {
        this.modService = modService;
        this.installerService = installerService;
        this.statusHolder = statusHolder;
    }

    public void setFlags(Long id, ModFlagsDto dto) {
        LocalMod mod = modService.requireMod(id);
        mod.setLoadOnClient(dto.getLoadOnClient());
        mod.setLoadOnServer(dto.getLoadOnServer());
        mod.setLoadOnHeadlessClient(dto.getLoadOnHeadlessClient());
        modService.saveMod(mod);
    }

    public void startSync() {
        if (!syncRunning.compareAndSet(false, true)) {
            log.warn("Local mod sync already in progress, ignoring request");
            return;
        }
        statusHolder.setStatus(LocalModSyncStatus.IN_PROGRESS);
        CompletableFuture.runAsync(() -> {
            try {
                installerService.syncAllMods();
                statusHolder.setStatus(LocalModSyncStatus.FINISHED);
            } catch (Exception e) {
                log.error("Local mod sync failed", e);
                statusHolder.setStatus(LocalModSyncStatus.ERROR);
            } finally {
                syncRunning.set(false);
            }
        });
    }

    public LocalModSyncStatus getSyncStatus() {
        return statusHolder.getStatus();
    }
}
