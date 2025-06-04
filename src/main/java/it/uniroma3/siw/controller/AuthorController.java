package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * Aggiunge, in tutte le view di questo controller,
     * l'attributo "currentUsername" con il nome dell’utente autenticato,
     * o null se il client non è loggato.
     */
    @ModelAttribute("currentUsername")
    public String populateCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }

    // ——————————————————————————————
    // List & Detail (accessibili a tutti)
    // ——————————————————————————————

    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "authors"; // Thymeleaf template: authors.html
    }

    @GetMapping("/{id}")
    public String showAuthor(@PathVariable Long id, Model model) {
        var authorOpt = authorService.findById(id);
        if (authorOpt.isEmpty()) {
            return "redirect:/authors?error=notfound";
        }
        model.addAttribute("author", authorOpt.get());
        return "author"; // Thymeleaf template: author.html
    }

    // ——————————————————————————————
    // Create (solo ADMIN)
    // ——————————————————————————————

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/add")
    public String addAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "add-author"; // Thymeleaf template: add-author.html
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public String addAuthorSubmit(@Valid @ModelAttribute("author") Author author,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "add-author";
        }
        authorService.save(author);
        return "redirect:/authors";
    }

    // ——————————————————————————————
    // Update (solo ADMIN)
    // ——————————————————————————————

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editAuthorForm(@PathVariable Long id, Model model) {
        var authorOpt = authorService.findById(id);
        if (authorOpt.isEmpty()) {
            return "redirect:/authors?error=notfound";
        }
        model.addAttribute("author", authorOpt.get());
        return "edit-author"; // Thymeleaf template: edit-author.html
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/edit/{id}")
    public String editAuthorSubmit(@PathVariable Long id,
                                   @Valid @ModelAttribute("author") Author author,
                                   BindingResult result) {
        if (result.hasErrors()) {
            return "edit-author";
        }
        author.setId(id);
        authorService.save(author);
        return "redirect:/authors/" + id;
    }

    // ——————————————————————————————
    // Delete (solo ADMIN)
    // ——————————————————————————————

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteById(id);
        return "redirect:/authors";
    }
}
