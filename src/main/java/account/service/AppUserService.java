package account.service;

import account.db.entity.AppUser;
import account.domain.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface AppUserService extends UserDetailsService {

    ChangePasswordResponse changePassword(AppUser appUser, ChangePasswordRequest changePasswordRequest);

    SignupResponse signup(SignupRequest request);

    Optional<AppUser> findUserByUsername(String s);

    void validatePassword(String password);

    List<UserResponse> getUsers();

    void deleteUser(String email);

    UpdateRolesResponse updateRoles(UpdateRolesRequest request);

    UpdateUserAccessResponse updateUserAccess(UpdateUserAccessRequest request);

    AppUser incrementLoginAttempts(AppUser appUser);

    void resetLoginAttempts(AppUser appUser);

    void lockUser(AppUser appUser);
}
