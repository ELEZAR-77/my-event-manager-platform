package dev.sorokin.kafka;

import java.time.LocalDateTime;
import java.util.List;

public class EventChangeKafkaMessage {
    private Long messageId;
    private String eventType;
    private Long eventId;
    private LocalDateTime occurredAt;
    private Long ownerId;
    private Long changedById;
    private List<Long> subscribers;
    private List<ChangeItem> changes;
}
