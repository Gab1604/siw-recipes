package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public Iterable<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }   

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    public Iterable<Review> findByRecipe(Recipe recipe) {
        return reviewRepository.findByRecipe(recipe);
    }

    public Review findByUserAndRecipe(User user, Recipe recipe) {
        return reviewRepository.findByUserAndRecipe(user, recipe);
    }
}
