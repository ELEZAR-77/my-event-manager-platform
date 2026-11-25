package dev.sorokin.eventmanager.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventmanager.web.ServerErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@AllArgsConstructor
@Slf4j
@Component
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    private  final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.error("Custom Authentication EntryPointHandler - commence - {}", authException.getMessage());

        log.info(request.getRequestURI());

        var messageResponse = new ServerErrorResponse(
                "Authentication Entry Point",
                authException.getMessage(),
                LocalDateTime.now()
        );

        var strResponse = objectMapper.writeValueAsString(messageResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(strResponse);
    }
}
