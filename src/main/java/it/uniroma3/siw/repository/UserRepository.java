package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trova tutti gli utenti con un dato cognome (ricerca parziale e ignorando maiuscole).
     */
    List<User> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Trova tutti gli utenti con un dato nome (ricerca parziale).
     */
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
}
