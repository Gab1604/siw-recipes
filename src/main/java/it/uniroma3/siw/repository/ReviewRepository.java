package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Recupera tutte le recensioni associate a un determinato libro (per ID libro).
     */
    List<Review> findByBook_Id(Long bookId);

    /**
     * Recupera tutte le recensioni scritte da un determinato utente (per ID utente).
     */
    List<Review> findByUser_Id(Long userId);
}
