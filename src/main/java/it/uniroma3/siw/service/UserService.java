package it.uniroma3.siw.service;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Salva o aggiorna un utente.
     * @param user l'utente da salvare
     * @return l'utente salvato
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Recupera un utente tramite ID.
     * @param id identificatore utente
     * @return utente se presente, altrimenti null
     */
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Restituisce tutti gli utenti registrati.
     * @return lista di utenti
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Elimina un utente tramite ID.
     * @param id identificatore dell'utente da eliminare
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
