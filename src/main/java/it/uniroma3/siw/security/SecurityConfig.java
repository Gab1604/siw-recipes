package it.uniroma3.siw.security;

import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomLoginSuccessHandler loginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Queste URL possono essere viste da chiunque (anonimo o autenticato):
                .requestMatchers(
                    "/", "/index",                    // home
                    "/login", "/register",            // pagine di login/registrazione
                    "/css/**", "/images/**", "/uploads/**",
                    "/books", "/books/**",            // elenco libri (anonimi possono vederli)
                    "/authors", "/authors/**"         // elenco autori (anonimi possono vederli)
                ).permitAll()

                // Qualsiasi URL che inizi con /admin/** è consentito SOLO a chi ha il ruolo ADMIN
                .requestMatchers("/admin/**").hasAuthority("ADMIN")

                // Tutte le altre richieste richiedono che l’utente sia autenticato
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(loginSuccessHandler)     // dopo login, gestisci il redirect personalizzato
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")                   // dopo il logout torno alla home
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(e -> e
                .accessDeniedPage("/access-denied")      // se un utente autenticato cerca di entrare in /admin senza permessi
            );

        return http.build();
    }
}
