
package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private BookService bookService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;


    @GetMapping("/account")
    public String getAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();
        if(credentials == null) {
            return "user/indexUser"; // oppure una pagina di errore più adatta
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
            model.addAttribute("error", "Credenziali non trovate");
            return "redirect:/user/account"; // Se non esiste, reindirizza alla pagina dell'account
        }
        // Aggiorna le credenziali con i nuovi dati
        userService.updateUser(updatedCredentials.getUser());
        credentialsService.updateCredentials(updatedCredentials);
        // 🔄 Riautentica l'utente con le nuove credenziali
        credentialsService.autoLogin(updatedCredentials.getUsername(), updatedCredentials.getPassword());
        return "redirect:/user/account";
    }


     @PostMapping("/deleteAccount/{id}")
    public String deleteAccount(@PathVariable("id") Long id, Model model) {
        Credentials credentials = credentialsService.getCredentials(id);
        if (credentials != null) {
            credentialsService.deleteCredentials(id);
            return "redirect:/logout";  // dopo eliminazione logout o redirect appropriato
        }
        model.addAttribute("error", "Utente non trovato");
        return "error";
    }

    /***********************************************************************/
    /**********************METODI PER BOOK**********************************/
    /***********************************************************************/
    @GetMapping("/book")
    public String showUserBooks(Model model){
        model.addAttribute("books", bookService.findAll());
        return "user/userBooks";
    }

    @GetMapping("/book/{id}")
    public String getBook(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = bookService.findById(id);
        if (!optionalBook.isPresent()) {
            model.addAttribute("errorMessage", "Libro non trovato.");
            return "redirect:/user/book"; // o pagina 404
        }
        Book book = optionalBook.get();
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviewService.findByBook(book));
        return "user/userBook";
    }

    @GetMapping("/book/{id}/addReview")
    public String showReviewForm(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = bookService.findById(id);
        if (!optionalBook.isPresent()) {
            model.addAttribute("errorMessage", "Libro non trovato.");
            return "redirect:/user/book"; // o pagina 404
        }
        Book book = optionalBook.get();
        model.addAttribute("book", book);
        model.addAttribute("review", new Review());
        return "user/newFormReview";  // template Thymeleaf per inserire recensione
    }

    // Salva recensione inviata dal form
    @PostMapping("/book/{id}/addReview")
    public String submitReview(@PathVariable Long id,
                            @ModelAttribute("review") Review review,
                            BindingResult result,
                            Principal principal,
                            Model model) {
        Optional<Book> optionalBook = bookService.findById(id);
        if (!optionalBook.isPresent()) {
            model.addAttribute("errorMessage", "Libro non trovato.");
            return "user/userBooks"; // o pagina 404
        }
        Book book = optionalBook.get();

        if (result.hasErrors()) {
            model.addAttribute("book", book);
            return "user/newFormReview";
        }
        review.setId(null);  // 🔑 Forza Hibernate a fare un INSERT, non UPDATE
        // Recupera le credenziali dall'username del principal
        String username = principal.getName();
        Credentials credentials = credentialsService.getCredentials(username);

        if (credentials == null) {
            return "redirect:/login";
        }

        // Ottieni l'utente associato alle credenziali
        User loggedUser = credentials.getUser();

        review.setBook(book);
        review.setUser(loggedUser);
        // Verifica se esiste già una recensione dello stesso utente per lo stesso libro
        Review existingReview = reviewService.findByUserAndBook(loggedUser, book);
        if (existingReview != null) {
            model.addAttribute("book", book);
            model.addAttribute("review", review);
            model.addAttribute("errorMessage", "Hai già inserito una recensione per questo libro.");
            return "user/newFormReview";
        }

        reviewService.save(review);

        return "redirect:/user/book/"+id;
    }

    @GetMapping("/book/search")
    public String searchBooks(@RequestParam("query") String query, Model model) {
        List<Book> books = bookService.findByTitleContainingIgnoreCase(query);
        model.addAttribute("books", books);
        return "user/userBooks"; // Nome del template HTML
    }

    /***********************************************************************/
    /**********************METODI PER AUTHOR********************************/
    /***********************************************************************/

    @GetMapping("/author")
    public String showAuthors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "user/userAuthors";
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable Long id, Model model) {
        Optional<Author> optionalAuthor = authorService.findById(id);
        if (!optionalAuthor.isPresent()) {
            model.addAttribute("errorMessage", "Autore non trovato.");
            return "redirect:/user/author"; // o pagina 404
        }
        Author author = optionalAuthor.get();
        model.addAttribute("author", author);
        return "user/userAuthor";
    }
    @GetMapping("/author/search")
    public String searchAuthors(@RequestParam("query") String query, Model model) {
        List<Author> authors = authorService.findByNameOrSurname(query);
        model.addAttribute("authors", authors);
        return "user/userAuthors"; // Nome del template HTML
    }
}
