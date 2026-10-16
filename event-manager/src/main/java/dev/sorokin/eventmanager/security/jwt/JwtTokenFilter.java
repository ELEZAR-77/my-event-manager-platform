package dev.sorokin.eventmanager.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventmanager.security.details.CustomUserDetails;
import dev.sorokin.eventmanager.security.details.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var header =  request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwtToken = header.substring(7);

        String loginFromToken;

        try {
            loginFromToken = jwtTokenManager.getLoginFromToken(jwtToken);

        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
            filterChain.doFilter(request, response);
            return;
        }

        var userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(loginFromToken);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("Authentication: " + authentication);
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }
}
