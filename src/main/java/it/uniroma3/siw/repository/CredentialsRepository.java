package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, Long> {

    /**
     * Trova le credenziali associate a uno username.
     * @param username lo username
     * @return Optional contenente le credenziali se esistono
     */
    Optional<Credentials> findByUsername(String username);
}
