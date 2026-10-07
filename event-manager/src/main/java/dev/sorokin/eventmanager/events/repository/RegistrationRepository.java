package dev.sorokin.eventmanager.events.repository;

import dev.sorokin.eventmanager.events.enity.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {
}