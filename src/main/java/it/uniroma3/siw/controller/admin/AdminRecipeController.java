package it.uniroma3.siw.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.RecipeService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminRecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ReviewService reviewService;

    /* ===== LISTA RICETTE ===== */

    @GetMapping("/recipes")
    public String showAdminRecipes(Model model) {
        model.addAttribute("recipes", recipeService.findAll());
        return "admin/adminRecipes";
    }

    /* ===== DETTAGLIO RICETTA ===== */

    @GetMapping("/recipes/{id}")
    public String getRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isEmpty()) {
            return "redirect:/admin/recipes";
        }
        Recipe recipe = optionalRecipe.get();
        model.addAttribute("recipe", recipe);
        model.addAttribute("reviews", reviewService.findByRecipe(recipe));
        return "admin/adminRecipe";
    }

    /* ===== NUOVA RICETTA ===== */

    @GetMapping("/recipes/new")
    public String getFormNewRecipe(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "admin/formNewRecipe";
    }

    @PostMapping("/recipes")
    public String addRecipe(@Valid @ModelAttribute("recipe") Recipe recipe,
                            BindingResult result,
                            @RequestParam(value = "recipeImages", required = false)
                            List<MultipartFile> images) throws IOException {

        if (result.hasErrors()) {
            return "admin/formNewRecipe";
        }

        recipeService.saveWithImages(recipe, images);
        return "redirect:/admin/recipes";
    }

    /* ===== MODIFICA RICETTA ===== */

    @GetMapping("/recipes/edit/{id}")
    public String editRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isEmpty()) {
            return "redirect:/admin/recipes";
        }
        model.addAttribute("recipe", optionalRecipe.get());
        return "admin/modificaRecipe";
    }

    @PostMapping("/recipes/{id}")
    public String updateRecipe(@PathVariable Long id,
                               @Valid @ModelAttribute("recipe") Recipe recipe,
                               BindingResult result,
                               @RequestParam(value = "recipeImages", required = false)
                               List<MultipartFile> images) throws IOException {

        if (result.hasErrors()) {
            return "admin/modificaRecipe";
        }

        recipe.setId(id);
        recipeService.saveWithImages(recipe, images);
        return "redirect:/admin/recipes";
    }

    /* ===== CANCELLA RICETTA ===== */

    @GetMapping("/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();

            if (recipe.getImages() != null) {
                for (Image img : recipe.getImages()) {
                    java.io.File file = new java.io.File("." + img.getPath());
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }

            recipeService.deleteById(id);
        }
        return "redirect:/admin/recipes";
    }

    /* ===== CANCELLA RECENSIONE ===== */

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        Optional<Review> optionalReview = reviewService.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            Long recipeId = review.getRecipe().getId();
            reviewService.deleteById(id);
            return "redirect:/admin/recipes/" + recipeId;
        }
        return "redirect:/admin/recipes";
    }
}
