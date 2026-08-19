package dev.sorokin.eventmanager.location.service;

import dev.sorokin.eventmanager.location.converter.LocationMapper;
import dev.sorokin.eventmanager.location.entity.Location;
import dev.sorokin.eventmanager.location.exceptions.LocationNameIsAlreadyExist;
import dev.sorokin.eventmanager.location.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public Location createLocation(Location location) {

        if (locationRepository.existsByName(location.name())) {
            throw new LocationNameIsAlreadyExist(
                    "Location with name " + location.name() + " already exists"
            );
        }


        var entity = locationRepository.save(locationMapper.domainToEntity(location));

        System.out.println(entity);

        return locationMapper.entityToDomain(entity);
    }


    public List<Location> getAllLocations() {

        return locationRepository.findAll()
                .stream()
                .map(locationMapper::entityToDomain)
                .toList();
    }

    @Transactional
    public Location updateLocation(Long id, Location location) {

        var entity = locationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entity with id %s not found".formatted(id))
        );

        if (location.name() != null) {
            entity.setName(location.name());
        }

        if (location.address() != null) {
            entity.setAddress(location.address());
        }

        if (location.capacity() != null) {
            entity.setCapacity(location.capacity());
        }

        if (location.description() != null) {
            entity.setDescription(location.description());
        }

        return locationMapper.entityToDomain(entity);
    }

    public Location getLocationById(Long id) {

        var entity = locationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entity with id %s not found".formatted(id))
        );

        return locationMapper.entityToDomain(entity);
    }


    public void deleteLocationById(Long id) {

        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("Entity with id %s not found".formatted(id));
        }

        locationRepository.deleteById(id);
    }
}
