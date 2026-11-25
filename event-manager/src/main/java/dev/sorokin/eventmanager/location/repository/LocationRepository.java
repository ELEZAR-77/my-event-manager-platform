package dev.sorokin.eventmanager.location.repository;

import dev.sorokin.eventmanager.location.entity.LocationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    boolean existsByName(String name);

    boolean existsByAddress(String address);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT l from LocationEntity l
                WHERE l.id = :locationId
        """)
    Optional<LocationEntity> findByIdForModify(@Param("locationId") Long locationId);
}
