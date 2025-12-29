package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /* =======================
       CRUD
       ======================= */

    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional
    public Iterable<Credentials> findAll() {
        return credentialsRepository.findAll();
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        return credentialsRepository.save(credentials);
    }

    @Transactional
    public void updateCredentials(Credentials credentials) {
        Credentials existing = this.getCredentials(credentials.getId());
        if (existing != null) {
            existing.setUsername(credentials.getUsername());

            if (credentials.getPassword() != null && !credentials.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(credentials.getPassword()));
            }

            existing.setRole(credentials.getRole());
            credentialsRepository.save(existing);
        }
    }

    @Transactional
    public void deleteCredentials(Long id) {
        Credentials credentials = this.getCredentials(id);
        if (credentials != null) {
            User user = credentials.getUser();
            if (user != null) {
                user.setCredentials(null);
                userRepository.delete(user);
            }
            credentialsRepository.delete(credentials);
        }
    }

    /* =======================
       CURRENT USER
       ======================= */

    @Transactional
    public Credentials getCurrentCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return this.getCredentials(userDetails.getUsername());
        }
        return null;
    }

    /* =======================
       AUTO LOGIN
       ======================= */

    public void autoLogin(String username, String password) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
