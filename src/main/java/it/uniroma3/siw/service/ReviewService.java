package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Restituisce tutte le recensioni presenti nel database.
     * @return lista di recensioni
     */
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    /**
     * Trova una recensione tramite ID.
     * @param id identificatore della recensione
     * @return Optional contenente la recensione se esiste
     */
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    /**
     * Salva o aggiorna una recensione.
     * @param review la recensione da salvare
     * @return la recensione salvata
     */
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    /**
     * Elimina una recensione.
     * @param review la recensione da eliminare
     */
    public void delete(Review review) {
        reviewRepository.delete(review);
    }

    /**
     * Elimina una recensione tramite ID.
     * @param id identificatore della recensione
     */
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}
