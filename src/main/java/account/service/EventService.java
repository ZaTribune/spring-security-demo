package account.service;

import account.db.entity.SecurityEvent;

import java.util.List;

public interface EventService {

    List<SecurityEvent> getSecurityEvents();
    void logEvent(SecurityEvent event);
}
