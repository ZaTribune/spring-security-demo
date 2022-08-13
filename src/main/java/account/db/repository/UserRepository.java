package account.db.repository;

import account.db.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser>findAppUserByUsernameIgnoreCase(String username);
    Optional<AppUser>findAppUserByUsername(String username);
}
