package dev.sorokin.eventmanager.events.repository;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.sorokin.eventmanager.events.enity.Event;
import dev.sorokin.eventmanager.events.enity.EventEntity;
import dev.sorokin.eventmanager.location.entity.LocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    boolean existsByName(@Param("name") String name);

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


    @Query("""
    SELECT event from EventEntity event
        WHERE (:name IS NULL OR event.name = :name)
            AND (:placesMin IS NULL OR event.maxPlaces >= :placesMin)
                AND (:placesMax IS NULL OR event.maxPlaces <= :placesMax)
                    AND (CAST(:dateStartAfter AS timestamp) IS NULL
                        OR event.startAt >= :dateStartAfter)
                        AND (CAST(:dateStartBefore AS timestamp) IS NULL
                             OR event.startAt <= :dateStartBefore)
                            AND(:costMin IS NULL OR event.cost >= :costMin)
                                AND (:costMax IS NULL OR event.cost <= :costMax)
                                    AND (:durationMin IS NULL OR event.durationMinutes >= :durationMin)
                                        AND (:durationMax IS NULL OR event.durationMinutes <= :durationMax)
                                            AND (:locationId IS NULL OR event.location.id = :locationId)
                                                AND (:eventStatus IS NULL OR event.eventStatus = :eventStatus)
    """)
    List<EventEntity> findByFiler(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Long locationId,
            @Param("eventStatus") String eventStatus
    );

    @Query("""
        SELECT e from EventEntity e
                WHERE (e.owner.id = :ownerId)
        """)
    List<EventEntity> findCurrentUserEvents(@Param("ownerId") Long ownerId);

    @Query("""
        SELECT e from EventEntity e
                WHERE e.location.id = :locationId
                        AND e.eventStatus = 'WAIT_START'
        """)
    List<EventEntity> findAllByLocationId(@Param("locationId") Long locationId);

    boolean existsByLocation_Id(Long id);

    @Query("""
        SELECT e from EventEntity e
            left join fetch e.location
                    left join fetch e.owner
                            where e.id IN :ids
        
        """)
    List<EventEntity> findAllByIdInWithDetails(@Param("ids") Collection<Long> ids);
}