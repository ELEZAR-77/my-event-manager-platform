package dev.sorokin.eventmanager.location.controller;

import dev.sorokin.eventmanager.location.converter.LocationMapper;
import dev.sorokin.eventmanager.location.dto.CreateLocationDto;
import dev.sorokin.eventmanager.location.dto.LocationDto;
import dev.sorokin.eventmanager.location.dto.UpdateLocationDto;
import dev.sorokin.eventmanager.location.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final LocationMapper locationMapper;



    @Operation(
            summary = "Создание локации, roles=[ADMIN]"
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LocationDto> createLocation(
                @Valid @RequestBody CreateLocationDto createLocationDto
        ) {

        log.info("Got request for create location");


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationMapper.toDto(
                        locationService.createLocation(
                                locationMapper.createDtoToDomain(createLocationDto)
                        )
                ));
    }

    @Operation(
            summary = "Получение списка локаций, roles=[ADMIN, USER]"
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public List<LocationDto> getLocations() {

        log.info("Got request for get locations list");

        return locationService.getAllLocations().stream()
                .map(locationMapper::toDto)
                .toList();
    }

    @Operation(
            summary = "Изменение локации, roles=[ADMIN]"
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLocationDto updateLocationDto
            ) {

        log.info("Got request for update location");

        return ResponseEntity
                .ok(
                        locationMapper.toDto(
                                locationService.updateLocation(
                                        id,
                                        locationMapper.updateToDomain(updateLocationDto)
                                )
                        )
                );
    }

    @Operation(
            summary = "Получение локации по Id, roles=[ADMIN, USER]"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {

        log.info("Got request for get location by id: {}", id);


        return ResponseEntity
                .ok(
                        locationMapper.toDto(
                                locationService.getLocationById(id)
                        )
                );
    }

    @Operation(
            summary = "Удаление локации, roles=[ADMIN]"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLocationById(@PathVariable Long id) {

        log.info("Got request for delete location by id: {}", id);

        locationService.deleteLocationById(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
