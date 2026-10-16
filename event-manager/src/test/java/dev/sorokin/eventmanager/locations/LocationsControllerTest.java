package dev.sorokin.eventmanager.locations;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.sorokin.eventmanager.AbstractTest;
import dev.sorokin.eventmanager.location.dto.CreateLocationDto;
import dev.sorokin.eventmanager.location.dto.LocationDto;
import dev.sorokin.eventmanager.location.dto.UpdateLocationDto;
import dev.sorokin.eventmanager.location.entity.Location;
import dev.sorokin.eventmanager.location.repository.LocationRepository;
import dev.sorokin.eventmanager.location.service.LocationService;
import dev.sorokin.eventmanager.user.entity.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class LocationsControllerTest extends AbstractTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationService locationService;


    @Test
    void shouldSuccessCreateLocation() throws Exception {

        var location = new CreateLocationDto(
                "some-name",
                "some-address",
                2000,
                "some-description"
        );

        var strRequest = objectMapper.writeValueAsString(location);

        var strResponse = mockMvc.perform(post("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(strRequest)
                .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        var responseObj = objectMapper.readValue(strResponse, Location.class);

        Assertions.assertEquals(responseObj.name(), location.name());
        Assertions.assertTrue(locationRepository.existsById(responseObj.id()));
        Assertions.assertTrue(locationRepository.existsByName(responseObj.name()));
    }


    @Test
    void shouldNotCreateLocation() throws Exception {

        var location = new CreateLocationDto(
                null,
                "some-address",
                2000,
                "some-description"
        );

        var strRequest = objectMapper.writeValueAsString(location);

       mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(strRequest)
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().isBadRequest());

       Assertions.assertFalse(locationRepository.existsByAddress(location.address()));
    }

    @Test
    void shouldAdminSuccessGetAllLocations() throws Exception {

        createDummyLocation();



        String response = mockMvc.perform(get("/locations")
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<LocationDto> responseObjectList = objectMapper.readValue(response, new  TypeReference<>() {});

        Assertions.assertNotNull(responseObjectList);
    }

    @Test
    void shouldUserSuccessGetAllLocations() throws Exception {

        createDummyLocation();



        String response = mockMvc.perform(get("/locations")
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.USER))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<LocationDto> responseObjectList = objectMapper.readValue(response, new  TypeReference<>() {});

        Assertions.assertNotNull(responseObjectList);
    }

    @Test
    void shouldSuccessUpdateLocation() throws Exception {

        var location = createDummyLocation();

        UpdateLocationDto updateLocationDto = new UpdateLocationDto(
                "update-name",
                null,
                null,
                "update-description"
        );

        var strRequest = objectMapper.writeValueAsString(updateLocationDto);


        String response = mockMvc.perform(patch("/locations/{id}", location.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(strRequest)
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        var  responseObj = objectMapper.readValue(response, Location.class);

        Assertions.assertNotEquals(responseObj.name(), location.name());
        Assertions.assertEquals(responseObj.address(), location.address());
        Assertions.assertEquals(responseObj.capacity(), location.capacity());
        Assertions.assertNotEquals(responseObj.description(), location.description());
        Assertions.assertTrue(locationRepository.existsById(responseObj.id()));
    }

    @Test
    void shouldCatchForbiddenWhenUserTriesUpdateLocation() throws Exception {

        var location = createDummyLocation();

        UpdateLocationDto updateLocationDto = new UpdateLocationDto(
                "update-name",
                null,
                null,
                "update-description"
        );

        var strRequest = objectMapper.writeValueAsString(updateLocationDto);


        mockMvc.perform(patch("/locations/{id}", location.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(strRequest)
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.USER))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldSuccessDeleteLocation() throws Exception {

        var location = createDummyLocation();

        mockMvc.perform(delete("/locations/{id}", location.id())
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(locationRepository.existsById(location.id()));
    }

    @Test
    void shouldCatchForbiddenWhenUserTriesDeleteLocation() throws Exception {

        var location = createDummyLocation();

        mockMvc.perform(delete("/locations/{id}", location.id())
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.USER))
                )
                .andExpect(status().isForbidden());

        Assertions.assertTrue(locationRepository.existsById(location.id()));
    }

    private Location createDummyLocation() {
        return locationService.createLocation(new Location(
                null,
                "some-name",
                "some-address",
                2000,
                "some-description"
        ));
    }
}
