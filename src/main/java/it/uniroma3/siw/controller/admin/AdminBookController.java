
package it.uniroma3.siw.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
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

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminBookController {
    
    @Autowired
    private BookService bookService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthorService authorService;


    @GetMapping("/book")
    public String showAdminBooks(Model model){
        model.addAttribute("books", bookService.findAll());
        return "admin/adminBooks";
    }

    @GetMapping("/book/{id}")
    public String getBook(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = bookService.findById(id);
        if (!optionalBook.isPresent()) {
            model.addAttribute("errorMessage", "Libro non trovato.");
            return "redirect:/admin/book"; // o pagina 404
        }
        Book book = optionalBook.get();
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviewService.findByBook(book));
        return "admin/adminBook";
    }

    @GetMapping("/formNewBook")
    public String getFormNewBook(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("allAuthors", authorService.findAll());
        return "admin/formNewBook";
    }

    @GetMapping("/deleteBook/{id}")
    public String deleteBook(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = this.bookService.findById(id);
        if (!optionalBook.isPresent()) {
            model.addAttribute("errorMessage", "Libro non trovato.");
            return "redirect:/admin/book";
        }
        Book book = optionalBook.get();

        // Cancella i file delle immagini fisiche
        if (book.getImages() != null) {
            for (Image img : book.getImages()) {
                String path = img.getPath();  
                java.io.File file = new java.io.File("." + path); //DA RIVEDERE
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        // Cancella il libro dal database
        this.bookService.deleteById(id);

        return "redirect:/admin/book";
    }

    @GetMapping("/modificaBook/{id}")
    public String modificaBook(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = this.bookService.findById(id);
        if (!optionalBook.isPresent()) {
            model.addAttribute("errorMessage", "Libro non trovato.");
            return "redirect:/admin/book";
        }
        model.addAttribute("book", optionalBook.get());
        model.addAttribute("allAuthors", authorService.findAll());
        return "admin/modificaBook";
       // model.addAttribute("book", this.bookService.findById(id));
       // return "admin/modificaBook";
    }

    @GetMapping("/modificaAutori/{id}")
    public String modificaAutori(@PathVariable Long id, Model model) {
        List<Author> authors = this.authorsToAdd(id);
        model.addAttribute("book", this.bookService.findById(id));
        model.addAttribute("autoriLibro", authors);
        return "admin/autorePerLibro";
    }

    private List<Author> authorsToAdd(Long bookId) {
        List<Author> authors = new ArrayList<>();

        for (Author author : this.authorService.findAuthorsNotInBook(bookId)) {
            authors.add(author);
        }
        return authors;
    }

    @PostMapping("/book/{id}")
    public String updateBook(@PathVariable Long id,
                            @Valid@ModelAttribute("book") Book book,
                            BindingResult result,
                            @RequestParam("bookImages") MultipartFile[] files,
                            @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                            Model model) throws IOException {
        if (result.hasErrors()) {
            return "admin/modificaBook";
        }

        book.setId(id); // Assicurati che l'ID sia corretto

        List<MultipartFile> imageList = List.of(files); // Converti array in lista

        // Associa gli autori selezionati
        if (authorIds != null) {
            for (Long authorId : authorIds) {
                Optional<Author> optionalAuthor = authorService.findById(authorId);
                Author author = optionalAuthor.get();
                if (author != null) {
                    book.addAuthor(author);
                }
            }
        }

        this.bookService.saveWithImages(book, imageList);

        return "redirect:/admin/book";
    }

    @PostMapping("/book")
    public String addBook(@Valid@ModelAttribute("book") Book book,
                        BindingResult result,
                        @RequestParam("bookImages") List<MultipartFile> images,
                        @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                        Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("allAuthors", authorService.findAll());
            return "admin/formNewBook";
        }
        //associa gli autori selezionati
        if (authorIds != null) {
            for (Long authorId : authorIds) {
                Optional<Author> optionalAuthor = authorService.findById(authorId);
                Author author = optionalAuthor.get();
                if (author != null) {
                    book.addAuthor(author);
                }
            }
        }

        this.bookService.saveWithImages(book, images);
        return "redirect:/admin/book";
    }

    

    @GetMapping("/deleteReview/{id}")
    public String deleteReview(@PathVariable Long id, Model model) {
        Optional<Review> optionalReview = reviewService.findById(id);

        if (!optionalReview.isPresent()) {
            model.addAttribute("errorMessage", "Recensione non trovata.");
            return "redirect:/admin/book";
        }

        Review review = optionalReview.get();
        Long bookId = review.getBook().getId(); // utile per redirect alla pagina del libro

        reviewService.deleteById(id);
        return "redirect:/admin/book/" + bookId;
    }

    @GetMapping("/addAuthorToBook/{bookId}/{authorId}")
    public String addAuthorToBook(@PathVariable Long bookId, @PathVariable Long authorId, Model model) {
        bookService.addAuthorToBook(bookId, authorId);
        return "redirect:/admin/modificaAutori/" + bookId;
    }

    @GetMapping("/book/search")
    public String searchBooks(@RequestParam("query") String query, Model model) {
        List<Book> books = bookService.findByTitleContainingIgnoreCase(query);
        model.addAttribute("books", books);
        return "admin/adminBooks"; // Nome del template HTML
    }
}
