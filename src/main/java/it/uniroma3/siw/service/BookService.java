package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.AuthorRepository;
import it.uniroma3.siw.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    /** Trova un libro per ID */
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    /** Restituisce tutti i libri */
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /** Ricerca libri per titolo (case-insensitive, match parziale) */
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /** Elimina un libro */
    public void delete(Book book) {
        bookRepository.delete(book);
    }

    /** Elimina un libro per ID */
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    /** Salvataggio semplice (senza immagine o autori) */
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    /** Salva un nuovo libro con immagine e autori */
    @Transactional
    public void saveWithImageAndAuthors(Book book, MultipartFile imageFile, List<Long> authorIds) {
        if (!imageFile.isEmpty()) {
            String imagePath = storeImage(imageFile);
            book.setImagePath(imagePath);
        }

        List<Author> authors = authorRepository.findAllById(authorIds);
        book.setAuthors(new HashSet<>(authors));

        bookRepository.save(book);
    }

    /** Aggiorna un libro esistente con eventuale nuova immagine e autori */
    @Transactional
    public void updateWithImageAndAuthors(Book book, MultipartFile imageFile, List<Long> authorIds) {
        if (!imageFile.isEmpty()) {
            String imagePath = storeImage(imageFile);
            book.setImagePath(imagePath);
        }

        List<Author> authors = authorRepository.findAllById(authorIds);
        book.setAuthors(new HashSet<>(authors));

        bookRepository.save(book);
    }

    /** Salva un file immagine sul disco e restituisce il path accessibile dal browser */
    private String storeImage(MultipartFile imageFile) {
        try {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/" + fileName);
            Files.createDirectories(uploadPath.getParent());
            Files.copy(imageFile.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Errore durante il salvataggio dell'immagine", e);
        }
    }
}
