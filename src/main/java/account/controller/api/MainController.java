package account.controller.api;


import account.db.entity.AppUser;
import account.db.entity.SecurityEvent;
import account.domain.*;
import account.service.EventService;
import account.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@RequestMapping("/api/auth")
@AllArgsConstructor
@RestController
public class MainController {

    private final UserService authenticationService;
    private final EventService eventService;


    @PostMapping("/signup")
    public SignupResponse signup(HttpServletRequest request, @RequestBody @Valid SignupRequest signupRequest){
        eventService.logEvent(
                SecurityEvent.builder()
                        .date(LocalDateTime.now().toString())
                        .action(Event.CREATE_USER)
                        .subject("Anonymous")
                        .object(signupRequest.getEmail().toLowerCase())
                        .path(request.getServletPath())
                        .build()

        );
        return authenticationService.signup(signupRequest);
    }

    @PostMapping("/changepass")
    public ChangePasswordResponse changePassword(HttpServletRequest request,
                                                 @AuthenticationPrincipal AppUser appUser,
                                                 @RequestBody @Valid ChangePasswordRequest changePasswordRequest){

        eventService.logEvent(
                SecurityEvent.builder()
                        .date(LocalDateTime.now().toString())
                        .action(Event.CHANGE_PASSWORD)
                        .subject(appUser.getUsername())
                        .object(appUser.getUsername())
                        .path(request.getServletPath())
                        .build()
        );

        return authenticationService.changePassword(appUser,changePasswordRequest);
    }



}
