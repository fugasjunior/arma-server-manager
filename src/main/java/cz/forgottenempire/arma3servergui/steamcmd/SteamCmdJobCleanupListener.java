package cz.forgottenempire.arma3servergui.steamcmd;

import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob.JobStatus;
import cz.forgottenempire.arma3servergui.steamcmd.repositories.SteamCmdJobRepository;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SteamCmdJobCleanupListener {

    private final SteamCmdJobRepository steamCmdJobRepository;

    @Autowired
    public SteamCmdJobCleanupListener(SteamCmdJobRepository steamCmdJobRepository) {
        this.steamCmdJobRepository = steamCmdJobRepository;
    }

    @EventListener
    public void setInterruptedJobsStatusAfterRestart(ContextStartedEvent cse) {
        List<SteamCmdJob> interruptedJobs = steamCmdJobRepository.findAllByStateIn(
                Set.of(JobStatus.QUEUED, JobStatus.RUNNING));
        interruptedJobs.forEach(job -> {
            String relatedEntity = getRelatedEntityName(job);
            log.warn("SteamCMD Job {} for {} was interrupted in {} status",
                    job.getId(), relatedEntity, job.getState());
            job.setState(JobStatus.INTERRUPTED);
            steamCmdJobRepository.save(job);
        });
    }

    private String getRelatedEntityName(SteamCmdJob job) {
        String relatedEntity = "unknown entity";
        WorkshopMod relatedMod = job.getRelatedWorkshopMod();
        if (relatedMod != null) {
            relatedEntity = "Workshop mod '" + relatedMod.getName() + "' (ID " + relatedMod.getId() + ")";
        }
        else if (job.getRelatedServer() != null) {
            relatedEntity = "Server " + job.getRelatedServer().name();
        }
        return relatedEntity;
    }
}
