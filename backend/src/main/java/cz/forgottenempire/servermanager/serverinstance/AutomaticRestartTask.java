package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.serverinstance.process.ServerProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.TaskScheduler;

import java.time.*;
import java.util.concurrent.ScheduledFuture;

@Configurable
@Slf4j
public class AutomaticRestartTask {

    private Clock clock;
    private TaskScheduler taskScheduler;
    private final ServerProcess serverProcess;
    private final LocalTime restartTime;
    private ScheduledFuture<?> job;

    public AutomaticRestartTask(ServerProcess serverProcess, LocalTime restartTime) {
        this.serverProcess = serverProcess;
        this.restartTime = restartTime;
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

    @Autowired
    void setClock(Clock clock) {
        this.clock = clock;
    }

    @Autowired
    void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }
}
