package dev.sorokin.eventmanager.events.repository;

import dev.sorokin.eventmanager.events.enity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    boolean existsByName(String name);
}