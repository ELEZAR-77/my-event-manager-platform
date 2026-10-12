package dev.sorokin.eventmanager.events.scheduler;

import dev.sorokin.eventmanager.events.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStatusScheduler {

    private final SchedulerConfiguration schedulerConfiguration;
    private final EventService eventService;

    @Scheduled(
            initialDelayString = "${my-scheduler.initial-delay-seconds}",
            fixedDelayString = "${my-scheduler.fixed-delay-seconds}",
            timeUnit = TimeUnit.SECONDS
    )
    public void updateEventStatus() {
        if (!schedulerConfiguration.enabled()) {
            log.warn("Scheduler disabled. Do nothing");
            return;
        }

        log.info("Start events statuses check");

        LocalDateTime now = LocalDateTime.now();
        int pageSize = 10;

        boolean hasMoreStarts = true;
        while (hasMoreStarts) {
            hasMoreStarts = eventService.startExpiredEvents(now, pageSize);
        }

        int page = 0;
        while (true) {

            int fetchedCount = eventService.finishExpiredEvents(now, pageSize, page);

            if (fetchedCount == 0) {
                break;
            }
            if (fetchedCount < pageSize) {
                break;
            }

            page++;
        }
        log.info("End of event status check");
    }
}
