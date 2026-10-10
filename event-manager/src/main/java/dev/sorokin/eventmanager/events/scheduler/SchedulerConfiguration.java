package dev.sorokin.eventmanager.events.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("my-scheduler")
public record SchedulerConfiguration(
        boolean enabled,
        int fixedDurationSeconds,
        int initialDelaySeconds,
        int fixedRateSeconds
) {
}
