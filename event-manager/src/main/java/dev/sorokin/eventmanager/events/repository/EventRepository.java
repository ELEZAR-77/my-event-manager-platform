package dev.sorokin.eventmanager.events.repository;

import dev.sorokin.eventmanager.events.enity.EventEntity;
import dev.sorokin.eventmanager.location.entity.LocationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    boolean existsByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e from EventEntity e where e.id = :id
        """)
    Optional<EventEntity> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT e from EventEntity e 
        WHERE e.eventStatus = 'WAIT_START' AND e.startAt <= :now
    """)
    List<EventEntity> findEventsToStart(LocalDateTime now, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT e from EventEntity e 
        WHERE e.eventStatus = 'STARTED'
    """)
    List<EventEntity> findStartedEvents(LocalDateTime now, Pageable pageable);

}