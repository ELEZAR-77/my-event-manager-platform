package dev.sorokin.eventmanager.user;


import com.fasterxml.jackson.core.type.TypeReference;

import dev.sorokin.eventmanager.AbstractTest;
import dev.sorokin.eventmanager.security.jwt.JwtTokenManager;
import dev.sorokin.eventmanager.user.dto.SignInRequest;
import dev.sorokin.eventmanager.user.dto.UserRegistrationRequest;
import dev.sorokin.eventmanager.user.dto.UserUpdateRequest;
import dev.sorokin.eventmanager.user.entity.User;
import dev.sorokin.eventmanager.user.entity.enums.UserRole;
import dev.sorokin.eventmanager.user.repository.UserRepository;
import dev.sorokin.eventmanager.user.serivce.UserService;
import lombok.RequiredArgsConstructor;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;

import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class UserControllerTest extends AbstractTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Test
    void shouldRegisterUser() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "some-loginn",
                30,
                "some-password"
        );

        var strRequest = objectMapper.writeValueAsString(request);

        var strResponse = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(strRequest)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        var responseObj = objectMapper.readValue(strResponse, User.class);

        Assertions.assertEquals(responseObj.login(), request.login());
        Assertions.assertNotEquals(responseObj.password(), request.password());
        assertTrue(userRepository.existsById(responseObj.id()));
    }


    @Test
    void shouldSuccessSignIn() throws Exception {
        SignInRequest signInRequest = new SignInRequest(
                "some-login",
                "some-password"
        );

        var strRequest = objectMapper.writeValueAsString(signInRequest);

        var responseToken = mockMvc.perform(post("/users/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(strRequest)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(userRepository.existsByLogin(signInRequest.login()));
        assertTrue(jwtTokenManager.validateToken(responseToken));
    }

    @Test
    void shouldNotSuccessAuthenticateUser() throws Exception {
        SignInRequest signInRequest = new SignInRequest(
                "some",
                "some-password"
        );

        var strRequest = objectMapper.writeValueAsString(signInRequest);

        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(strRequest)
                )
                .andExpect(status().isUnauthorized());

        Assertions.assertFalse(userRepository.existsByLogin(signInRequest.login()));
    }

    @Test
    void shouldSuccessGetUsers() throws Exception {
        var strResponse = mockMvc.perform(get("/users")
                .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<User> users =  objectMapper.readValue(
                strResponse,
                new TypeReference<>() {}
        );

        assertThat(users)
                .isNotEmpty()
                .allSatisfy(user ->
                        assertTrue(userRepository.existsById(user.id())
                                            && userRepository.existsByLogin(user.login())
                        )
                );
    }

    @Test
    void shouldUserTriesGetUsers() throws Exception {
        mockMvc.perform(get("/users")
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.USER))
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void shouldSuccessGetUserById() throws Exception {

        var user = createUser();

        var strResponse = mockMvc.perform(get("/users/{id}", user.id())
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var responseObj = objectMapper.readValue(strResponse, User.class);

        Assertions.assertEquals(responseObj.login(), user.login());
        Assertions.assertNotEquals(responseObj.password(), user.password());
        assertTrue(userRepository.existsById(user.id()));
    }

    @Test
    void shouldSuccessUpdateUser() throws Exception {

        var dummyUser = createUser();

        UserUpdateRequest request = new UserUpdateRequest(
                "new-login",
                null,
                "new-password",
                "ADMIN"
        );

        String strRequest = objectMapper.writeValueAsString(request);

        var strResponse = mockMvc.perform(patch("/users/{id}", dummyUser.id())
                .contentType(MediaType.APPLICATION_JSON)
                        .content(strRequest)
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var responseObj = objectMapper.readValue(strResponse, User.class);

        Assertions.assertNotEquals(responseObj.login(), dummyUser.login());
        Assertions.assertNotEquals(responseObj.password(), dummyUser.password());
        Assertions.assertNotEquals(responseObj.role(), dummyUser.role());
        Assertions.assertTrue(userRepository.existsById(dummyUser.id()));
    }

    @Test
    void shouldSuccessDeleteUser() throws Exception {
        var userForDelete = createUser();

        mockMvc.perform(delete("/users/{id}", userForDelete.id())
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(userRepository.existsById(userForDelete.id()));
    }


    private User createUser() {
        return userService.createUser(new User(
                null,
                "dummy-user",
                30,
                "dummy-password",
                UserRole.USER
        ));
    }


    @BeforeEach
    public void setUp() {
        userService.createUser(new User(
                null,
                "some-login",
                30,
                "some-password",
                UserRole.USER
        ));
    }
}
