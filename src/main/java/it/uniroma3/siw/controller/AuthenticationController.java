package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.RecipeService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private RecipeService recipeService;

    /* =====================
       LOGIN
       ===================== */

    @GetMapping("/login")
    public String showLoginForm() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "login";
    }

    /* =====================
       REGISTER
       ===================== */

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }

        User user = new User();
        user.setCredentials(new Credentials());

        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        Credentials credentials = user.getCredentials();
        credentials.setUser(user);
        credentials.setRole(Credentials.DEFAULT_ROLE);

        credentialsService.saveCredentials(credentials);

        return "redirect:/login";
    }

    /* =====================
       ROOT "/"
       ===================== */

    @GetMapping("/")
    public String index() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index";
        }

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        Credentials credentials =
                credentialsService.getCredentials(userDetails.getUsername());

        if (Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
            return "admin/indexAdmin";
        }

        return "redirect:/indexUser";
    }

    /* =====================
       HOME USER
       ===================== */

    @GetMapping("/indexUser")
    public String userHome(Model model) {
        model.addAttribute("latestRecipes",
                recipeService.findLatestRecipes());
        return "user/indexUser";
    }

    @GetMapping("/indexAdmin")
    public String adminHome() {
        return "admin/indexAdmin";
    }
}
