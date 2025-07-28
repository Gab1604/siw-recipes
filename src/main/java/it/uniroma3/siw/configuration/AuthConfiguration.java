package it.uniroma3.siw.configuration;

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

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.model.Credentials.DEFAULT_ROLE;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
//public  class WebSecurityConfig {
	public class AuthConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .authoritiesByUsernameQuery("SELECT username, role from credentials WHERE username=?")
                .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/**").permitAll()
                    // chiunque (autenticato o no) può accedere alle pagine index, login, register, ai css e alle immagini
                    .requestMatchers(HttpMethod.GET,"/","/index","/register", "/login", "/book/**", "/author/**", "/css/**", "/images/**", "/uploads/**").permitAll()
                    // chiunque (autenticato o no) può mandare richieste POST al punto di accesso per login e register 
                    .requestMatchers(HttpMethod.POST,"/register", "/login").permitAll()
                    .requestMatchers(HttpMethod.GET,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
                    .requestMatchers(HttpMethod.POST,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
                    //gli user possono accedere alle pagine /user/**, ma non possono accedere a /admin/**
                    .requestMatchers(HttpMethod.GET,"/user/**").hasAnyAuthority(DEFAULT_ROLE)
                    .requestMatchers(HttpMethod.POST,"/user/**").hasAnyAuthority(DEFAULT_ROLE)
                    // tutti gli utenti autenticati possono accere alle pagine rimanenti 
                    .anyRequest().authenticated()
                )
                // LOGIN: qui definiamo il login
                .formLogin(form -> form
                    .loginPage("/login")
                    .successHandler((request, response, authentication) -> {
                        // Se è admin -> vai su /indexAdmin
                        var authorities = authentication.getAuthorities();
                        boolean isAdmin = authorities.stream()
                                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
                        if (isAdmin) {
                            response.sendRedirect("/indexAdmin");
                        } else {
                            response.sendRedirect("/indexUser"); // o un'altra pagina per utenti normali
                        }
                    })
                    .failureUrl("/login?error=true")
                    .permitAll()
                )


                /*.formLogin(form -> form
                    .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/success", true)
                    .failureUrl("/login?error=true")
                )*/
                // LOGOUT: qui definiamo il logout
                .logout(logout -> logout
                    // il logout è attivato con una richiesta GET a "/logout"
                    .logoutUrl("/logout")
                    // in caso di successo, si viene reindirizzati alla home
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .clearAuthentication(true)
                    .permitAll()
                );
        return httpSecurity.build();
    }
    
}