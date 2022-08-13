package account.controller.api;


import account.db.entity.AppUser;
import account.db.entity.SecurityEvent;
import account.service.EventService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api")
@AllArgsConstructor
@RestController
public class AuditingController {

    private final EventService service;

    @GetMapping("/security/events")
    public Object getSecurityEvents(@AuthenticationPrincipal AppUser appUser){
        log.info("Getting security events for user {}",appUser);

        List<SecurityEvent>list= service.getSecurityEvents();

        return list.isEmpty()?new ArrayNode(JsonNodeFactory.instance):list;
    }


}
