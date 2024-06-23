package account.config;


import account.db.entity.AppUser;
import account.db.entity.Role;
import account.service.AppUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Slf4j
@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private AppUserService userDetailsService;

    private PasswordEncoder passwordEncoder;

    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        provider.setPostAuthenticationChecks(toCheck -> {
            AppUser appUser = (AppUser) toCheck;
            if (appUser.getLoginAttempts() > 0) {
                log.info("resetting login attempts - {}", appUser);
                userDetailsService.resetLoginAttempts(appUser);
            }
        });
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authenticationProvider(authenticationProvider())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(config ->
                        config.accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(restAuthenticationEntryPoint)
                )
                .headers(config -> config.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(config -> config
                        .requestMatchers("/h2-console/**").permitAll()
                        // anyone
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/actuator/shutdown").hasAuthority(Role.ROLE_ADMINISTRATOR.name())

                        //users & accountants & admins
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()

                        //users & accountants
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment/**")
                        .hasAnyAuthority(Role.ROLE_ADMINISTRATOR.name(), Role.ROLE_USER.name(), Role.ROLE_ACCOUNTANT.name())

                        //accountants only
                        .requestMatchers("/api/acct/payments/**").hasAnyAuthority(Role.ROLE_ADMINISTRATOR.name(), Role.ROLE_ACCOUNTANT.name())
                        //admin only
                        .requestMatchers("/api/admin/**").hasAuthority(Role.ROLE_ADMINISTRATOR.name())

                        //auditor only
                        .requestMatchers(HttpMethod.GET, "/api/security/events/**").hasAnyAuthority(Role.ROLE_ADMINISTRATOR.name(), Role.ROLE_AUDITOR.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // no session
        return httpSecurity.build();
    }
}
