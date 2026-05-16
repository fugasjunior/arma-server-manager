package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.serverinstance.process.ServerProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import java.time.*;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class AutomaticRestartTask {

    private final Clock clock;
    private final TaskScheduler taskScheduler;
    private final ServerProcess serverProcess;
    private final LocalTime restartTime;
    private ScheduledFuture<?> job;

    public AutomaticRestartTask(ServerProcess serverProcess, LocalTime restartTime, Clock clock, TaskScheduler taskScheduler) {
        this.serverProcess = serverProcess;
        this.restartTime = restartTime;
        this.clock = clock;
        this.taskScheduler = taskScheduler;
    }

    public AutomaticRestartTask schedule() {
        Instant nextRestartTime = getNearestFutureInstantOf(restartTime);
        job = taskScheduler.scheduleAtFixedRate(serverProcess::restart, nextRestartTime, Duration.ofDays(1));
        log.info("Scheduling restart for server ID {} at {}", serverProcess.getServerId(),
                nextRestartTime.atZone(ZoneId.systemDefault()).toLocalDateTime());
        return this;
    }

    public void cancel() {
        job.cancel(false);
    }

    private Instant getNearestFutureInstantOf(LocalTime localTime) {
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        LocalDateTime upcomingDateTime = LocalDateTime.of(currentDateTime.toLocalDate(), localTime);
        if (currentDateTime.isAfter(upcomingDateTime)) {
            upcomingDateTime = upcomingDateTime.plusDays(1);
        }
        return upcomingDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
