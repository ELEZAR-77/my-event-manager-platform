package dev.sorokin.eventmanager.events.repository;

import dev.sorokin.eventmanager.events.enity.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
        SELECT r from RegistrationEntity r
                WHERE r.event.id = :eventId
        """)
    Optional<RegistrationEntity> findByEventId(Long eventId);


    @Query("""
        SELECT r from RegistrationEntity r
                WHERE r.user.id = :userId
        """)
    List<RegistrationEntity> findAllByUserId(@Param("userId") Long userId);
}