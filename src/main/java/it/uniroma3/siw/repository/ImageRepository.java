package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Trova tutte le immagini associate a un libro dato il suo ID.
     */
    List<Image> findByBook_Id(Long bookId);

    /**
     * Trova un'immagine tramite nome file (opzionale, utile per download o confronto).
     */
    Optional<Image> findByFilename(String filename);
}
