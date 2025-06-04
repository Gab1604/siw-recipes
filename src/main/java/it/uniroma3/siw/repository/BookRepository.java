package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Ricerca libri per titolo (case-insensitive, partial match).
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Trova tutti i libri pubblicati in un dato anno.
     */
    List<Book> findByPublicationYear(Integer year);

    /**
     * Trova tutti i libri scritti da un dato autore (tramite ID).
     */
    List<Book> findByAuthors_Id(Long authorId);

    // Esempio avanzato:
    // List<Book> findByPublicationYearBetween(Integer startYear, Integer endYear);
}
