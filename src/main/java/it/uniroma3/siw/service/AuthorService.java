
package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.AuthorRepository;


@Service
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private ImageStorageService imageStorageService;



    public Author save(Author author) {
        return authorRepository.save(author);
    }

    public Iterable<Author> findAll() {
        return authorRepository.findAll();
    }

    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }
    
    public Iterable<Author> findAuthorsNotInBook(Long bookId) {
        return authorRepository.findAuthorsNotInBook(bookId);
    }
    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }

    @Transactional
    public Author saveWithImage(Author author, MultipartFile imageFile) throws IOException {

        // Salva l'autore inizialmente per avere un ID (serve per il path immagine)
        author = this.authorRepository.save(author);

        // Gestione dell'immagine
        if (imageFile != null && !imageFile.isEmpty()) {
            String path = this.imageStorageService.store(imageFile, "author/" + author.getId());

            Image image = new Image();
            image.setPath(path);
            image.setAuthor(author);    // Associa autore
            author.setImage(image);     // Associa immagine all'autore
        }

        // Salva autore con immagine associata
        return this.authorRepository.save(author);
    }
    
    public Optional<Author> findByIdWithBooks(Long id) {
        Author author = authorRepository.findByIdWithBooks(id);
        return Optional.ofNullable(author);
    }

    public List<Author> findByNameOrSurname(String query) {
        return authorRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(query, query);
    }

}
