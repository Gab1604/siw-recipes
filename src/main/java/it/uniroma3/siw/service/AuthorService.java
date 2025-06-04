package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Salva o aggiorna un autore.
     * @param author l'autore da salvare
     * @return l'autore salvato
     */
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    /**
     * Trova un autore per ID.
     * @param id identificatore dell'autore
     * @return Optional contenente l'autore se esiste
     */
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    /**
     * Restituisce la lista di tutti gli autori.
     * @return lista di autori
     */
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    /**
     * Elimina un autore.
     * @param author l'autore da eliminare
     */
    public void delete(Author author) {
        authorRepository.delete(author);
    }

    /**
     * Elimina un autore dato il suo ID.
     * @param id identificatore dell'autore
     */
    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }
}
