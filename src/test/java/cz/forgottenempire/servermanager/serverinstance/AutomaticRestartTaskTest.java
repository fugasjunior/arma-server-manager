package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.serverinstance.process.ServerProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import java.time.*;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutomaticRestartTaskTest {

    @Mock(stubOnly = true)
    private ServerProcess serverProcess;
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private ScheduledFuture<?> scheduledFuture;

    private Clock fixedClock;
    private ZoneId zone;
    private LocalDate currentDate;
    private LocalTime currentTime;

    @BeforeEach
    void setUp() {
        zone = ZoneId.systemDefault();
        currentDate = LocalDate.of(2023, 11, 30);
        currentTime = LocalTime.of(10, 0);
        ZonedDateTime currentDateTime = ZonedDateTime.of(currentDate, currentTime, zone);
        fixedClock = Clock.fixed(currentDateTime.toInstant(), zone);
    }

    @Test
    void schedule_whenCurrentTimeIsBeforeRestartTime_thenFirstRestartIsScheduledForTheSameDay() {
        LocalTime timeAfterCurrentTime = currentTime.plusHours(1);
        AutomaticRestartTask automaticRestartTask = createAutomaticRestartTask(timeAfterCurrentTime);

        automaticRestartTask.schedule();

        Instant expectedTime = ZonedDateTime.of(currentDate, timeAfterCurrentTime, zone).toInstant();
        verify(taskScheduler).scheduleAtFixedRate(any(), eq(expectedTime), eq(Duration.ofDays(1)));
    }

    @Test
    void schedule_whenCurrentTimeIsAfterRestartTime_thenFirstRestartIsScheduledForTheNextDay() {
        LocalTime timeBeforeCurrentTime = currentTime.minusHours(1);
        AutomaticRestartTask automaticRestartTask = createAutomaticRestartTask(timeBeforeCurrentTime);

        automaticRestartTask.schedule();

        Instant expectedTime = ZonedDateTime.of(currentDate.plusDays(1), timeBeforeCurrentTime, zone).toInstant();
        verify(taskScheduler).scheduleAtFixedRate(any(), eq(expectedTime), eq(Duration.ofDays(1)));
    }

    @Test
    void cancel_whenCalled_scheduledFutureIsCancelled() {
        doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(any(), any(), any());
        AutomaticRestartTask automaticRestartTask = createAutomaticRestartTask(currentTime);
        automaticRestartTask.schedule();

        automaticRestartTask.cancel();

        verify(scheduledFuture).cancel(false);
    }

    private AutomaticRestartTask createAutomaticRestartTask(LocalTime restartTime) {
        AutomaticRestartTask automaticRestartTask = new AutomaticRestartTask(serverProcess, restartTime);
        automaticRestartTask.setTaskScheduler(taskScheduler);
        automaticRestartTask.setClock(fixedClock);
        return automaticRestartTask;
    }
}
