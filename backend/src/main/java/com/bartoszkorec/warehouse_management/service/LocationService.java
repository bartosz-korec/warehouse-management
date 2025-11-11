package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.Location;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.repository.LocationRepository;
import com.bartoszkorec.warehouse_management.utils.LocationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationDto getStartingLocationDto() {
        return getStartingLocation()
                .map(LocationHelper::toDto)
                .orElseThrow(() -> new IllegalStateException("Starting location not set"));
    }

    public LocationDto createLocation(LocationDto locationDto) {
        if (locationDto.locationType() == LocationType.STARTING_POINT && getStartingLocation().isPresent()) {
            throw new IllegalStateException("Starting location already set");
        }
        Location location = LocationHelper.toEntity(locationDto);
        location.setId(null);
        location = locationRepository.save(location);
        return LocationHelper.toDto(location);
    }

    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(LocationHelper::toDto)
                .toList();
    }

    public long getLocationCount() {
        return locationRepository.count();
    }

    private Optional<Location> getStartingLocation() {
        return locationRepository.findByTypeEquals(LocationType.STARTING_POINT)
                .stream().findAny();
    }
}
