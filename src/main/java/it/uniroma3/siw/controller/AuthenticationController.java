package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") User user,
                                  BindingResult userBindingResult,
                                  @Valid @ModelAttribute("credentials") Credentials credentials,
                                  BindingResult credentialsBindingResult,
                                  Model model) {

        if (credentialsService.getCredentials(credentials.getUsername()) != null)
            credentialsBindingResult.rejectValue("username", "error.credentials", "Username già esistente");

        if (userBindingResult.hasErrors() || credentialsBindingResult.hasErrors())
            return "register";

        credentials.setUser(user);
        credentialsService.saveCredentials(credentials);

        // Autenticazione automatica post-registrazione
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(credentials.getUsername())
                .password(credentials.getPassword()) // già codificata
                .authorities(credentials.getRole())
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        return "redirect:/success";
    }

    @GetMapping("/success")
    public String defaultAfterLogin() {
        // Redireziona in base al ruolo
        Credentials credentials = credentialsService.getCredentials(
                SecurityContextHolder.getContext().getAuthentication().getName());

        if (credentials.getRole().equals(Credentials.ADMIN_ROLE))
            return "redirect:/admin/dashboard";
        return "redirect:/books";
    }
}
