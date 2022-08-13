package account.db.repository;


import account.db.entity.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<SecurityEvent, Long> {


}
