package cz.forgottenempire.servermanager.serverinstance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class AutomaticRestartScheduler {

    private final TaskScheduler taskScheduler;
    private final Clock clock;
    private final Map<Long, ScheduledFuture<?>> jobs = new ConcurrentHashMap<>();

    @Autowired
    public AutomaticRestartScheduler(TaskScheduler taskScheduler, Clock clock) {
        this.taskScheduler = taskScheduler;
        this.clock = clock;
    }

    public void schedule(long serverId, LocalTime time, Runnable callback) {
        cancel(serverId);
        Instant nextFire = nextInstant(time);
        log.info("Scheduling automatic restart for server ID {} at {}", serverId,
                nextFire.atZone(ZoneId.systemDefault()).toLocalDateTime());
        jobs.put(serverId, taskScheduler.scheduleAtFixedRate(callback, nextFire, Duration.ofDays(1)));
    }

    public void cancel(long serverId) {
        ScheduledFuture<?> existing = jobs.remove(serverId);
        if (existing != null) {
            existing.cancel(false);
        }
    }

    private Instant nextInstant(LocalTime time) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime next = LocalDateTime.of(now.toLocalDate(), time);
        if (now.isAfter(next)) {
            next = next.plusDays(1);
        }
        return next.atZone(ZoneId.systemDefault()).toInstant();
    }
}
