package dev.sorokin.eventmanager.location.converter;

import dev.sorokin.eventmanager.location.dto.CreateLocationDto;
import dev.sorokin.eventmanager.location.dto.LocationDto;
import dev.sorokin.eventmanager.location.dto.UpdateLocationDto;
import dev.sorokin.eventmanager.location.entity.Location;
import dev.sorokin.eventmanager.location.entity.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }

    public Location createDtoToDomain(CreateLocationDto createLocationDto) {
        return new Location(
                null,
                createLocationDto.name(),
                createLocationDto.address(),
                createLocationDto.capacity(),
                createLocationDto.description()
        );
    }

    public Location updateToDomain(UpdateLocationDto updateLocationDto) {
        return new Location(
                null,
                updateLocationDto.name(),
                updateLocationDto.address(),
                updateLocationDto.capacity(),
                updateLocationDto.description()
        );
    }

    public LocationEntity domainToEntity(Location location) {
        return new LocationEntity(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }

    public Location entityToDomain(LocationEntity entity) {
        return new Location(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCapacity(),
                entity.getDescription()
        );
    }
}
