package dev.sorokin.eventmanager.events.scheduler;

import dev.sorokin.eventmanager.events.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        if (schedulerConfiguration.enabled()) {
            log.info("Start events statuses check");

            eventService.updateEventStatuses();

            log.info("End of event status check");

        } else {
            log.warn("Scheduler disabled. Do nothing");
        }
    }
}
