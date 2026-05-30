package cz.forgottenempire.servermanager.serverinstance;

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
class AutomaticRestartSchedulerTest {

    private static final long SERVER_ID = 1L;

    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private ScheduledFuture<?> scheduledFuture;

    private ZoneId zone;
    private LocalDate currentDate;
    private LocalTime currentTime;
    private AutomaticRestartScheduler scheduler;

    @BeforeEach
    void setUp() {
        zone = ZoneId.systemDefault();
        currentDate = LocalDate.of(2023, 11, 30);
        currentTime = LocalTime.of(10, 0);
        ZonedDateTime currentDateTime = ZonedDateTime.of(currentDate, currentTime, zone);
        Clock fixedClock = Clock.fixed(currentDateTime.toInstant(), zone);
        scheduler = new AutomaticRestartScheduler(taskScheduler, fixedClock);
        lenient().doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(any(), any(Instant.class), any());
    }

    @Test
    void schedule_whenCurrentTimeIsBeforeRestartTime_thenFirstRestartIsScheduledForTheSameDay() {
        LocalTime timeAfterCurrentTime = currentTime.plusHours(1);
        Runnable callback = mock(Runnable.class, withSettings().stubOnly());

        scheduler.schedule(SERVER_ID, timeAfterCurrentTime, callback);

        Instant expectedTime = ZonedDateTime.of(currentDate, timeAfterCurrentTime, zone).toInstant();
        verify(taskScheduler).scheduleAtFixedRate(any(), eq(expectedTime), eq(Duration.ofDays(1)));
    }

    @Test
    void schedule_whenCurrentTimeIsAfterRestartTime_thenFirstRestartIsScheduledForTheNextDay() {
        LocalTime timeBeforeCurrentTime = currentTime.minusHours(1);
        Runnable callback = mock(Runnable.class, withSettings().stubOnly());

        scheduler.schedule(SERVER_ID, timeBeforeCurrentTime, callback);

        Instant expectedTime = ZonedDateTime.of(currentDate.plusDays(1), timeBeforeCurrentTime, zone).toInstant();
        verify(taskScheduler).scheduleAtFixedRate(any(), eq(expectedTime), eq(Duration.ofDays(1)));
    }

    @Test
    void cancel_whenJobExists_thenScheduledFutureIsCancelled() {
        doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(any(), any(Instant.class), any());
        scheduler.schedule(SERVER_ID, currentTime, mock(Runnable.class, withSettings().stubOnly()));

        scheduler.cancel(SERVER_ID);

        verify(scheduledFuture).cancel(false);
    }

    @Test
    void cancel_whenNoJobExists_thenNoExceptionIsThrown() {
        scheduler.cancel(SERVER_ID);

        verifyNoInteractions(taskScheduler);
    }

    @Test
    void schedule_whenCalledTwice_thenPreviousJobIsCancelledBeforeSchedulingNew() {
        doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(any(), any(Instant.class), any());
        Runnable first = mock(Runnable.class, withSettings().stubOnly());
        Runnable second = mock(Runnable.class, withSettings().stubOnly());

        scheduler.schedule(SERVER_ID, currentTime.plusHours(1), first);
        scheduler.schedule(SERVER_ID, currentTime.plusHours(2), second);

        verify(scheduledFuture).cancel(false);
        verify(taskScheduler, times(2)).scheduleAtFixedRate(any(), any(Instant.class), any());
    }
}
