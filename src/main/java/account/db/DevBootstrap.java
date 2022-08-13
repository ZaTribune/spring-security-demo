package account.db;

import account.db.entity.SecurityEvent;
import account.db.repository.EventRepository;
import account.domain.Event;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class DevBootstrap implements CommandLineRunner {


    private final EventRepository repository;


    @Override
    public void run(String... args) {

        Optional<SecurityEvent> event = repository.findById(1L);
        event.ifPresent(e -> {
            if (e.getSubject().equals("maxmustermann@acme.com")
                    && e.getAction().equals(Event.LOGIN_FAILED)
                    && e.getObject().equals("/api/empl/payment")
                    && e.getPath().equals("/api/empl/payment"))
                repository.delete(e);
        });

    }
}
