package account.service;

import account.db.entity.AppUser;
import account.db.entity.Role;
import account.db.repository.UserRepository;
import account.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements AppUserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(acme\\.)+[a-zA-Z]{2,6}$";
    Pattern pattern = Pattern.compile(regex);

    Predicate<SignupRequest> validateEmail =
            signupRequest -> pattern.matcher(signupRequest.getEmail()).matches();


    @Override
    public ChangePasswordResponse changePassword(AppUser appUser, ChangePasswordRequest changePasswordRequest) {
        log.warn("change password");
        validatePassword(changePasswordRequest.getNewPassword());


        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), appUser.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");


        appUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        appUser.setLoginAttempts(0);

        userRepository.save(appUser);
        return ChangePasswordResponse.builder()
                .email(appUser.getUsername().toLowerCase())
                .status("The password has been updated successfully")
                .build();
    }

    @Override
    public SignupResponse signup(SignupRequest request) {
        log.warn("sign up");
        if (!validateEmail.test(request)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad Request", null);

        }

        validatePassword(request.getPassword());

        userRepository.findAppUserByUsernameIgnoreCase(request.getEmail()).ifPresent(u -> {
            log.error("user {} already exists!", request.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        });

        Role roles;
        if (userRepository.count() == 0)
            roles = Role.ROLE_ADMINISTRATOR;
        else
            roles = Role.ROLE_USER;


        AppUser user = AppUser.builder()
                .name(request.getName())
                .lastname(request.getLastname())
                .username(request.getEmail().toLowerCase())//tricky
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .loginAttempts(0)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user.grantAuthority(roles);

        user = userRepository.save(user);

        return SignupResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getUsername())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public AppUser loadUserByUsername(String s) throws UsernameNotFoundException {
        //log.warn("loading user {}", s);
        return userRepository.findAppUserByUsernameIgnoreCase(s)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

    }

    @Override
    public Optional<AppUser> findUserByUsername(String s) {
        return userRepository.findAppUserByUsernameIgnoreCase(s);
    }

    @Override
    public void validatePassword(String password) {
        log.warn("validating password");
        if (password.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");
        }
        if (preachedPasswords.contains(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
    }

    @Override
    public List<UserResponse> getUsers() {

        return userRepository.findAll(Sort.by("id").ascending())
                .stream()
                .map(user ->
                        UserResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .lastname(user.getLastname())
                                .email(user.getUsername())
                                .roles(user.getRoles())
                                .build())
                .collect(Collectors.toList());

    }

    @Override
    public void deleteUser(String email) {

        AppUser appUser = loadUserByUsername(email);
        userRepository.delete(appUser);
    }

    @Override
    public UpdateRolesResponse updateRoles(UpdateRolesRequest request) {

        AppUser appUser = loadUserByUsername(request.getUser());
        Role role;

        role = Role.valueOfLabel(request.getRole());

        if (role == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");

        if (Operation.GRANT.equals(request.getOperation())) {

            if (role.getGroup()!=appUser.getRoles().get(0).getGroup())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");

            appUser.grantAuthority(role);
        } else {
            if (Role.ROLE_ADMINISTRATOR.equals(role))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");

            if (appUser.getRoles().size() == 1 && appUser.getRoles().contains(role))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");

            if (!appUser.getRoles().contains(role))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");

            appUser.removeAuthority(role);
        }

        appUser = userRepository.save(appUser);

        return UpdateRolesResponse.builder()
                .id(appUser.getId())
                .name(appUser.getName())
                .lastname(appUser.getLastname())
                .email(appUser.getUsername())
                .roles(appUser.getRoles())
                .build();
    }

    @Override
    public UpdateUserAccessResponse updateUserAccess(UpdateUserAccessRequest request){

        AppUser appUser=loadUserByUsername(request.getUser());

        String message;

        if (appUser.getRoles().contains(Role.ROLE_ADMINISTRATOR))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");

        if (request.getOperation().equals(UpdateUserAccessRequest.Operation.UNLOCK)){
            appUser.setAccountNonLocked(true);
            appUser.setLoginAttempts(0);
            message=String.format("User %s unlocked!",appUser.getUsername());
        }else {
            appUser.setAccountNonLocked(false);
            message=String.format("User %s locked!",appUser.getUsername());
        }

        userRepository.save(appUser);

        return UpdateUserAccessResponse.builder()
                .status(message)
                .build();
    }

    @Override
    public AppUser incrementLoginAttempts(AppUser appUser){

      appUser.setLoginAttempts(appUser.getLoginAttempts()+1);
      return userRepository.save(appUser);
    }

    @Override
    public void resetLoginAttempts(AppUser appUser){
        appUser.setLoginAttempts(0);
        userRepository.save(appUser);
    }

    @Override
    public void lockUser(AppUser appUser){
        if (!appUser.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            appUser.setAccountNonLocked(false);
            userRepository.save(appUser);
        }
    }

    private final List<String> preachedPasswords = List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
}