package account.service;

import account.db.entity.SecurityEvent;
import account.db.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository repository;

    @Override
    public List<SecurityEvent> getSecurityEvents() {
        return repository.findAll(Sort.by("id").ascending());
    }

    @Override
    public void logEvent(SecurityEvent event) {
        repository.save(event);
    }


}
