package it.uniroma3.siw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Recipe;
import it.uniroma3.siw.service.RecipeService;
import it.uniroma3.siw.service.ReviewService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ReviewService reviewService;

    /* =======================
       LISTA RICETTE PUBBLICA
       ======================= */
    @GetMapping("/recipes")
    public String showRecipes(Model model) {
        model.addAttribute("recipes", recipeService.findAll());
        return "recipes";
    }

    /* =======================
       DETTAGLIO RICETTA
       ======================= */
    @GetMapping("/recipes/{id}")
    public String getRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isEmpty()) {
            return "redirect:/recipes";
        }

        Recipe recipe = optionalRecipe.get();
        model.addAttribute("recipe", recipe);
        model.addAttribute("reviews", reviewService.findByRecipe(recipe));
        return "recipe";
    }

    /* =======================
       RICERCA
       ======================= */
    @GetMapping("/recipes/search")
    public String searchRecipes(@RequestParam("query") String query, Model model) {
        List<Recipe> recipes =
                recipeService.findByTitleContainingIgnoreCase(query);
        model.addAttribute("recipes", recipes);
        return "recipes";
    }
}
