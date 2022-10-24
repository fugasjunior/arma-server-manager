package cz.forgottenempire.arma3servergui.steamcmd.repositories;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob.JobStatus;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SteamCmdJobRepository extends JpaRepository<SteamCmdJob, Long> {

    @Query("SELECT job " +
            "FROM SteamCmdJob job " +
            "WHERE job.relatedServer = ?1 " +
            "AND job.createdAt IN (SELECT MAX(createdAt) FROM SteamCmdJob WHERE job.relatedServer = ?1)")
    Optional<SteamCmdJob> findLatestStatusForServer(ServerType serverType);

    @Query("SELECT job " +
            "FROM SteamCmdJob job " +
            "WHERE job.relatedWorkshopMod = ?1 " +
            "AND job.createdAt IN (SELECT MAX(createdAt) FROM SteamCmdJob WHERE job.relatedWorkshopMod = ?1)")
    Optional<SteamCmdJob> findLatestJobForWorkshopMod(WorkshopMod workshopMod);

    List<SteamCmdJob> findAllByStateIn(Collection<JobStatus> statuses);

    List<SteamCmdJob> findAllByCreatedAtBefore(LocalDateTime localDateTime);
}
