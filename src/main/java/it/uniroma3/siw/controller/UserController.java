package it.uniroma3.siw.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    /* ===== ACCOUNT ===== */

    @GetMapping("/account")
    public String getAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();
        if (credentials == null) {
            return "user/indexUser";
        }
        model.addAttribute("credentials", credentials);
        return "user/userAccount";
    }

    @GetMapping("/modificaAccount")
    public String getModificaAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();
        model.addAttribute("credentials", credentials);
        return "user/userModificaAccount";
    }

    @PostMapping("/account")
    public String updateAccount(@ModelAttribute("credentials") Credentials updatedCredentials, Model model) {
        Credentials currentCredentials = credentialsService.getCurrentCredentials();
        if (currentCredentials == null || !updatedCredentials.getId().equals(currentCredentials.getId())) {
            return "redirect:/user/account";
        }
        userService.updateUser(updatedCredentials.getUser());
        credentialsService.updateCredentials(updatedCredentials);
        credentialsService.autoLogin(updatedCredentials.getUsername(), updatedCredentials.getPassword());
        return "redirect:/user/account";
    }

    @PostMapping("/deleteAccount/{id}")
    public String deleteAccount(@PathVariable("id") Long id) {
        Credentials credentials = credentialsService.getCredentials(id);
        if (credentials != null) {
            credentialsService.deleteCredentials(id);
            return "redirect:/logout";
        }
        return "error";
    }

    /* ===== RICETTE ===== */

    @GetMapping("/recipes")
    public String showUserRecipes(Model model) {
        model.addAttribute("recipes", recipeService.findAll());
        return "user/userRecipes";
    }

    @GetMapping("/recipes/{id}")
    public String getRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isEmpty()) {
            return "redirect:/user/recipes";
        }
        Recipe recipe = optionalRecipe.get();
        model.addAttribute("recipe", recipe);
        model.addAttribute("reviews", reviewService.findByRecipe(recipe));
        return "user/userRecipe";
    }

    @GetMapping("/recipes/{id}/addReview")
    public String showReviewForm(@PathVariable Long id, Model model) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isEmpty()) {
            return "redirect:/user/recipes";
        }
        model.addAttribute("recipe", optionalRecipe.get());
        model.addAttribute("review", new Review());
        return "user/newFormReview";
    }

    @PostMapping("/recipes/{id}/addReview")
    public String submitReview(@PathVariable Long id,
                               @ModelAttribute("review") Review review,
                               BindingResult result,
                               Principal principal,
                               Model model) {

        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isEmpty()) {
            return "redirect:/user/recipes";
        }

        Recipe recipe = optionalRecipe.get();

        if (result.hasErrors()) {
            model.addAttribute("recipe", recipe);
            return "user/newFormReview";
        }

        Credentials credentials = credentialsService.getCredentials(principal.getName());
        if (credentials == null) {
            return "redirect:/login";
        }

        User loggedUser = credentials.getUser();

        Review existingReview = reviewService.findByUserAndRecipe(loggedUser, recipe);
        if (existingReview != null) {
            model.addAttribute("recipe", recipe);
            model.addAttribute("review", review);
            model.addAttribute("errorMessage", "Hai già inserito una recensione per questa ricetta.");
            return "user/newFormReview";
        }

        review.setId(null);
        review.setRecipe(recipe);
        review.setUser(loggedUser);

        reviewService.save(review);
        return "redirect:/user/recipes/" + id;
    }

    @GetMapping("/recipes/search")
    public String searchRecipes(@RequestParam("query") String query, Model model) {
        List<Recipe> recipes = recipeService.findByTitleContainingIgnoreCase(query);
        model.addAttribute("recipes", recipes);
        return "user/userRecipes";
    }
}