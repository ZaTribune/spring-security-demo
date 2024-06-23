package account.controller.api;


import account.db.entity.AppUser;
import account.db.entity.Role;
import account.db.entity.SecurityEvent;
import account.domain.*;
import account.service.EventService;
import account.service.UserService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequestMapping("/api/admin")
@AllArgsConstructor
@RestController
public class AdminController {

    private final UserService userService;
    private final EventService eventService;

    @PutMapping("/user/role")
    public UpdateRolesResponse updateRoles(HttpServletRequest request,
                                           @AuthenticationPrincipal AppUser appuser,
                                           @RequestBody UpdateRolesRequest updateRolesRequest) {

        log.info("updating user roles {}", updateRolesRequest);
        Event event;
        String object;

        if (updateRolesRequest.getOperation().equals(Operation.GRANT)) {
            event = Event.GRANT_ROLE;
            object = String.format("Grant role %s to %s", updateRolesRequest.getRole()
                    , updateRolesRequest.getUser().toLowerCase());
        } else {
            event = Event.REMOVE_ROLE;
            object = String.format("Remove role %s from %s", updateRolesRequest.getRole()
                    , updateRolesRequest.getUser().toLowerCase());
        }
        UpdateRolesResponse response = userService.updateRoles(updateRolesRequest);

        eventService.logEvent(
                SecurityEvent.builder()
                        .date(LocalDateTime.now().toString())
                        .action(event)
                        .subject(appuser.getUsername())
                        .object(object)
                        .path(request.getServletPath())
                        .build()
        );

        return response;
    }

    @DeleteMapping("/user/{email}")
    public DeleteUserResponse deleteUser(HttpServletRequest request,
                                         @AuthenticationPrincipal AppUser admin,
                                         @PathVariable String email) {

        log.info("deleting user {}", email);

        if (admin.getUsername().equals(email))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");

        userService.deleteUser(email);

        eventService.logEvent(
                SecurityEvent.builder()
                        .date(LocalDateTime.now().toString())
                        .action(Event.DELETE_USER)
                        .subject(admin.getUsername())
                        .object(email.toLowerCase())
                        .path(request.getServletPath())
                        .build()
        );

        return DeleteUserResponse.builder()
                .user(email)
                .status("Deleted successfully!")
                .build();
    }

    @GetMapping("/user")
    public Object getUserInformation(@AuthenticationPrincipal AppUser admin) {
        log.info("getting users' information for {}", admin);

        List<?> list = userService.getUsers();
        return list.isEmpty() ? new ArrayNode(JsonNodeFactory.instance) : list;
    }

    @PutMapping("/user/access")
    public UpdateUserAccessResponse unlockUser(HttpServletRequest request,
                                               @AuthenticationPrincipal AppUser admin,
                                               @RequestBody UpdateUserAccessRequest accessRequest) {
        log.info("Updating user access - {}", accessRequest);

        Event event;
        String object;
        if (accessRequest.getOperation().equals(UpdateUserAccessRequest.Operation.LOCK)) {
            event = Event.LOCK_USER;
            object = String.format("Lock user %s", accessRequest.getUser().toLowerCase());
        } else {
            event = Event.UNLOCK_USER;
            object = String.format("Unlock user %s", accessRequest.getUser().toLowerCase());
        }

        UpdateUserAccessResponse response = userService.updateUserAccess(accessRequest);
        eventService.logEvent(
                SecurityEvent.builder()
                        .date(LocalDateTime.now().toString())
                        .action(event)
                        .subject(admin.getUsername())
                        .object(object)
                        .path(request.getServletPath())
                        .build()
        );
        return response;
    }

}
