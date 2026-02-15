package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.RecipeRepository;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    public Recipe save(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public Iterable<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }

    public List<Recipe> findLatestRecipes() {
    return recipeRepository.findTop6ByOrderByIdDesc();
}

    public List<Recipe> findAllById(List<Long> ids) {
        return (List<Recipe>) recipeRepository.findAllById(ids);
    }

    public List<Recipe> findByCategory(String category) {
    return this.recipeRepository.findByCategory(category);
}


    public void deleteById(Long id) {
        recipeRepository.deleteById(id);
    }

    @Transactional
    public Recipe saveWithImages(Recipe recipe, List<MultipartFile> images) throws IOException {
        recipe = this.recipeRepository.save(recipe); // salva per avere l'ID

        List<Image> imgs = recipe.getImages();
        if (imgs == null)
            imgs = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile f : images) {
                if (!f.isEmpty()) {
                    String path = this.imageStorageService.store(f, "recipe/" + recipe.getId());

                    Image image = new Image();
                    image.setPath(path);
                    image.setRecipe(recipe);
                    imgs.add(image);
                }
            }
        }

        recipe.setImages(imgs);
        return this.recipeRepository.save(recipe);
    }

    public List<Recipe> findByTitleContainingIgnoreCase(String query) {
        return recipeRepository.findByTitleContainingIgnoreCase(query);
    }
}
