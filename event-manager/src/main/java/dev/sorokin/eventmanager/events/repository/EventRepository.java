package dev.sorokin.eventmanager.events.repository;

import dev.sorokin.eventmanager.events.enity.EventEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    boolean existsByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e from EventEntity e where e.id = :id
        """)
    Optional<EventEntity> findByIdForUpdate(@Param("id") Long id);
}