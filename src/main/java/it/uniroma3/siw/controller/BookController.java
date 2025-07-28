
package it.uniroma3.siw.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.AuthorService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class BookController {

    @Autowired BookService bookService;
    @Autowired AuthorService authorService;
    @Autowired ReviewService reviewService;
   

    @GetMapping("/book")
    public String showBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books";
    }

    @GetMapping("/book/{id}")
    public String getBook(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = bookService.findById(id);
        if (!optionalBook.isPresent()) {
            return "redirect:/book"; // o una pagina 404
        }
        Book book = optionalBook.get();
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviewService.findByBook(book)); 
        return "book";
    }
    @GetMapping("/book/search")
    public String searchBooks(@RequestParam("query") String query, Model model) {
        List<Book> books = bookService.findByTitleContainingIgnoreCase(query);
        model.addAttribute("books", books);
        return "books"; // Nome del template HTML
    }    
}
