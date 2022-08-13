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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;


@Slf4j
@Configuration
@AllArgsConstructor
//@EnableGlobalMethodSecurity(
//        prePostEnabled = true,
//        securedEnabled = true,
//        jsr250Enabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private AppUserService userDetailsService;

    private PasswordEncoder passwordEncoder;

    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        provider.setPostAuthenticationChecks(toCheck -> {
            AppUser appUser= (AppUser) toCheck;
            if (appUser.getLoginAttempts()>0) {
                log.info("resetting login attempts - {}",appUser);
               userDetailsService.resetLoginAttempts(appUser);
            }
        });
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handle auth error
                .and()
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests()
                // anyone
                .antMatchers(HttpMethod.POST, "/api/signup").permitAll()
                .antMatchers(HttpMethod.POST,"/actuator/shutdown").permitAll()

                //users & accountants & admins
                .antMatchers(HttpMethod.POST,"/api/auth/changepass")
                     .authenticated()

                //users & accountants
                .antMatchers(HttpMethod.GET,"/api/empl/payment/**")
                      .hasAnyAuthority(Role.ROLE_USER.name(),Role.ROLE_ACCOUNTANT.name())

                //accountant only
                .antMatchers(HttpMethod.POST,"/api/acct/payments")
                .hasAuthority(Role.ROLE_ACCOUNTANT.name())
                .antMatchers(HttpMethod.PUT,"/api/acct/payments")
                .hasAuthority(Role.ROLE_ACCOUNTANT.name())

                //admin only
                .antMatchers("/api/admin/**").hasAuthority(Role.ROLE_ADMINISTRATOR.name())

                //auditor only
                .antMatchers(HttpMethod.GET,"/api/security/events/**").hasAuthority(Role.ROLE_AUDITOR.name())

                .antMatchers(HttpMethod.GET,"/api/acct/payments/all").permitAll()//for testing
                .antMatchers(HttpMethod.GET).authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }
}
