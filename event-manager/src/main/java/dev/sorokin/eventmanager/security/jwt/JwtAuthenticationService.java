package dev.sorokin.eventmanager.security.jwt;


import dev.sorokin.eventmanager.security.details.CustomUserDetails;
import dev.sorokin.eventmanager.user.dto.SignInRequest;
import dev.sorokin.eventmanager.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;

    public String authenticateUser(SignInRequest signInRequest) {

        var user = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.login(),
                signInRequest.password()
        ));

        return jwtTokenManager.generateToken(user);
    }


    // Оставлю на будущее пускай тут сидит пока
    public User getCurrentAuthenticatedUserOrThrow() throws UsernameNotFoundException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UsernameNotFoundException("Authentication object is null");
        }

        var userDetails =  (CustomUserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            throw new UsernameNotFoundException("Authentication object is null");
        }

        return new User(
                userDetails.getUser().getId(),
                userDetails.getUser().getLogin(),
                userDetails.getUser().getAge(),
                userDetails.getUser().getPassword(),
                userDetails.getUser().getRole()
        );
    }
}
