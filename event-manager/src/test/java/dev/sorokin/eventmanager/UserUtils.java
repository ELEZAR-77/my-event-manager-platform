package dev.sorokin.eventmanager;


import dev.sorokin.eventmanager.security.jwt.JwtTokenManager;
import dev.sorokin.eventmanager.user.entity.UserEntity;
import dev.sorokin.eventmanager.user.entity.enums.UserRole;
import dev.sorokin.eventmanager.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtils {

    private final JwtTokenManager jwtTokenManager;
    private final UserRepository userRepository;

    private static final String DEFAULT_USER_LOGIN = "user";
    private static final String DEFAULT_ADMIN_LOGIN = "admin";

    private static boolean isInitialize = false;

    public String getJwtTokenWithRole(UserRole role) {
        if (!isInitialize
            || userRepository.existsByLogin(DEFAULT_USER_LOGIN)
            || userRepository.existsByLogin(DEFAULT_ADMIN_LOGIN)) {

            initializeTestUsers();

            isInitialize = userRepository.existsByLogin(DEFAULT_USER_LOGIN)
                    && userRepository.existsByLogin(DEFAULT_ADMIN_LOGIN);

        }

        return switch (role) {
            case ADMIN ->
                jwtTokenManager.generateTokenByLogin(DEFAULT_ADMIN_LOGIN);
            case USER ->
                jwtTokenManager.generateTokenByLogin(DEFAULT_USER_LOGIN);
        };
    }


    private void initializeTestUsers() {
        createUser(DEFAULT_ADMIN_LOGIN, 25, "admin", UserRole.ADMIN);
        createUser(DEFAULT_USER_LOGIN, 22, "user", UserRole.USER);
    }

    private void createUser(
            String login,
            Integer age,
            String password,
            UserRole role
    ) {
        if (userRepository.existsByLogin(login)) {
            return;
        }

        userRepository.save(new UserEntity(
                null,
                login,
                age,
                password,
                role
        ));
    }
}
