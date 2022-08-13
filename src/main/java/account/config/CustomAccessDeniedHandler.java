package account.config;

import account.db.entity.SecurityEvent;
import account.domain.Event;
import account.error.ErrorResponse;
import account.service.EventService;
import account.util.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper=new ObjectMapper();
    private final EventService eventService;

    @Autowired
    public CustomAccessDeniedHandler(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        log.error("Authorization error: {}",ex.getMessage());

        String emailDecoded= HttpUtils.extractUsernameBasicAuth(request);

        eventService.logEvent(
                SecurityEvent.builder()
                        .date(LocalDateTime.now().toString())
                        .action(Event.ACCESS_DENIED)
                        .subject(emailDecoded)
                        .object(request.getServletPath())
                        .path(request.getServletPath())
                        .build()

        );

        ErrorResponse errorResponse= ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("Access Denied!")
                .path(request.getServletPath())
                .build();

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
