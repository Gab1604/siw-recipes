
package it.uniroma3.siw.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


import org.springframework.security.authentication.AuthenticationManager;

@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected CredentialsRepository credentialsRepository;
    @Autowired
    protected AuthenticationManager authenticationManager;
    @Autowired
    protected UserRepository userRepository;

    public void autoLogin(String username, String rawPassword) {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(username, rawPassword);
        
        Authentication auth = authenticationManager.authenticate(token);

        if (auth.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialsRepository.save(credentials);
    }

    @Transactional
    public Iterable<Credentials> findAll() {
        return credentialsRepository.findAll();
    }

    @Transactional
    public Credentials getCurrentCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            Credentials credentials = this.getCredentials(username);
            return credentials;
        }
        return null;
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


    @Transactional
    public void updateCredentials(Credentials credentials) {
        Credentials existingCredentials = this.getCredentials(credentials.getId());
        if (existingCredentials != null) {
            existingCredentials.setUsername(credentials.getUsername());

            if (credentials.getPassword() != null && !credentials.getPassword().isBlank()) {
                existingCredentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
            }

            existingCredentials.setRole(credentials.getRole());

          
            if (existingCredentials.getUser() != null) {
                existingCredentials.getUser().setCredentials(existingCredentials);
            }

            this.credentialsRepository.save(existingCredentials);
        }
    }

}
