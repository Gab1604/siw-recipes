package it.uniroma3.siw.configuration;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.model.Credentials.DEFAULT_ROLE;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Autowired
    private DataSource dataSource;

    /* ============================
       AUTHENTICATION (JDBC)
       ============================ */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery(
                "SELECT username, password, true FROM credentials WHERE username=?"
            )
            .authoritiesByUsernameQuery(
                "SELECT username, role FROM credentials WHERE username=?"
            )
            .passwordEncoder(passwordEncoder());
    }

    /* ============================
       PASSWORD ENCODER
       ============================ */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* ============================
       AUTHENTICATION MANAGER
       ============================ */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /* ============================
       SECURITY FILTER CHAIN
       ============================ */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                /* ===== PUBBLICO ===== */
                .requestMatchers(
                        "/",
                        "/login",
                        "/register",
                        "/css/**",
                        "/images/**",
                        "/uploads/**",
                        "/book/**",
                        "/author/**"
                ).permitAll()

                /* ===== ADMIN ===== */
                .requestMatchers("/admin/**")
                .hasAuthority(ADMIN_ROLE)

                /* ===== USER ===== */
                .requestMatchers("/user/**")
                .hasAuthority(DEFAULT_ROLE)

                /* ===== TUTTO IL RESTO ===== */
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {

                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals(ADMIN_ROLE));

                    if (isAdmin) {
                        response.sendRedirect("/indexAdmin");
                    } else {
                        response.sendRedirect("/indexUser");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }
}
