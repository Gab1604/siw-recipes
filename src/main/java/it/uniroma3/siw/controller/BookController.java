package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.AuthorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;

    public BookController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    /**
     * Se l'utente è autenticato, inserisce in tutti i ModelAttribute
     * il nome utente corrente (username). Se non c'è autenticazione,
     * currentUsername sarà null.
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
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books";   // Thymeleaf template: books.html
    }

    @GetMapping("/{id}")
    public String showBook(@PathVariable Long id, Model model) {
        var bookOpt = bookService.findById(id);
        if (bookOpt.isEmpty()) {
            return "redirect:/books?error=notfound";
        }
        model.addAttribute("book", bookOpt.get());
        return "book";    // Thymeleaf template: book.html
    }

    // ——————————————————————————————
    // Create (solo ADMIN)
    // ——————————————————————————————

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "add-book"; // Thymeleaf template: add-book.html
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public String addBookSubmit(@Valid @ModelAttribute("book") Book book,
                                BindingResult bindingResult,
                                @RequestParam("cover") MultipartFile imageFile,
                                @RequestParam("authorIds") List<Long> authorIds,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            return "add-book";
        }

        bookService.saveWithImageAndAuthors(book, imageFile, authorIds);
        return "redirect:/books";
    }

    // ——————————————————————————————
    // Update (solo ADMIN)
    // ——————————————————————————————

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        var bookOpt = bookService.findById(id);
        if (bookOpt.isEmpty()) {
            return "redirect:/books?error=notfound";
        }
        model.addAttribute("book", bookOpt.get());
        model.addAttribute("authors", authorService.findAll());
        return "edit-book"; // Thymeleaf template: edit-book.html
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/edit/{id}")
    public String editBookSubmit(@PathVariable Long id,
                                 @Valid @ModelAttribute("book") Book book,
                                 BindingResult bindingResult,
                                 @RequestParam("cover") MultipartFile imageFile,
                                 @RequestParam("authorIds") List<Long> authorIds,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            return "edit-book";
        }

        // assicuriamoci che l’id sia corretto prima di salvare
        book.setId(id);
        bookService.updateWithImageAndAuthors(book, imageFile, authorIds);
        return "redirect:/books/" + id;
    }

    // ——————————————————————————————
    // Delete (solo ADMIN)
    // ——————————————————————————————

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }
}
