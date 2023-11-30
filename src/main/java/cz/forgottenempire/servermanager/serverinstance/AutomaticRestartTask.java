package cz.forgottenempire.servermanager.serverinstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.TaskScheduler;

import java.time.*;
import java.util.concurrent.ScheduledFuture;

@Configurable
public class AutomaticRestartTask {
    private final ServerProcess serverProcess;
    private final LocalTime restartTime;
    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> job;

    public AutomaticRestartTask(ServerProcess serverProcess, LocalTime restartTime) {
        this.serverProcess = serverProcess;
        this.restartTime = restartTime;
    }

    public AutomaticRestartTask schedule() {
        job = taskScheduler.scheduleAtFixedRate(serverProcess::restart, getNearestFutureInstantOf(restartTime), Duration.ofDays(1));
        return this;
    }

    public void cancel() {
        job.cancel(false);
    }

    private static Instant getNearestFutureInstantOf(LocalTime localTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime upcomingDateTime = LocalDateTime.of(currentDateTime.toLocalDate(), localTime);
        if (currentDateTime.isAfter(upcomingDateTime)) {
            upcomingDateTime = upcomingDateTime.plusDays(1);
        }
        return upcomingDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    @Autowired
    void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }
}
