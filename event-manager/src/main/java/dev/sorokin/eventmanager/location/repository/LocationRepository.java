package dev.sorokin.eventmanager.location.repository;

import dev.sorokin.eventmanager.location.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    boolean existsByName(String name);

    boolean existsByAddress(String address);
}
