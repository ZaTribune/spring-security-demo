package account.config;

import account.db.entity.SecurityEvent;
import account.domain.Event;
import account.error.ErrorResponse;
import account.service.EventService;
import account.service.UserService;
import account.util.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    private final EventService eventService;
    private final UserService userService;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Authentication error: {}", authException.getMessage());


        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Unauthorized")
                .path(request.getServletPath())
                .build();

        String emailDecoded = HttpUtils.extractUsernameBasicAuth(request);
        eventService.logEvent(
                SecurityEvent.builder()
                        .date(LocalDateTime.now().toString())
                        .action(Event.LOGIN_FAILED)
                        .subject(emailDecoded)
                        .object(request.getServletPath())
                        .path(request.getServletPath())
                        .build());

        userService.findUserByUsername(emailDecoded).ifPresentOrElse(appUser -> {
            appUser = userService.incrementLoginAttempts(appUser);
            if (appUser.getLoginAttempts() == 5) {
                log.error("lock user {} after 5 failed login attempts", emailDecoded);
                eventService.logEvent(
                        SecurityEvent.builder()
                                .date(LocalDateTime.now().toString())
                                .action(Event.BRUTE_FORCE)
                                .subject(emailDecoded)
                                .object(request.getServletPath())
                                .path(request.getServletPath())
                                .build()
                );
                if (appUser.isAccountNonLocked()) {
                    userService.lockUser(appUser);
                    eventService.logEvent(
                            SecurityEvent.builder()
                                    .date(LocalDateTime.now().toString())
                                    .action(Event.LOCK_USER)
                                    .subject(emailDecoded)
                                    .object(String.format("Lock user %s", emailDecoded))
                                    .path(request.getServletPath())
                                    .build()
                    );
                }
                errorResponse.setMessage("User account is locked");
            } else if (appUser.getLoginAttempts() < 5) {
                eventService.logEvent(
                        SecurityEvent.builder()
                                .date(LocalDateTime.now().toString())
                                .action(Event.LOGIN_FAILED)
                                .subject(emailDecoded)
                                .object(request.getServletPath())
                                .path(request.getServletPath())
                                .build());
                errorResponse.setMessage("Bad Credentials!");
            }

        }, () -> {
            if (!emailDecoded.equals("Anonymous"))
                eventService.logEvent(
                        SecurityEvent.builder()
                                .date(LocalDateTime.now().toString())
                                .action(Event.LOGIN_FAILED)
                                .subject(emailDecoded)
                                .object(request.getServletPath())
                                .path(request.getServletPath())
                                .build());
        });

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
