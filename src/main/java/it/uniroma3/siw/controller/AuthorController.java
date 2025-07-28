
package it.uniroma3.siw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class AuthorController {

    @Autowired AuthorService authorService;
    @Autowired BookService bookService;


    @GetMapping("/author")
    public String showAuthors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "authors";
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable Long id, Model model) {
        Optional<Author> optionalAuthor = authorService.findByIdWithBooks(id);
        if (!optionalAuthor.isPresent()) {
            return "redirect:/author"; // o una pagina 404
        }
        Author author = optionalAuthor.get();
        model.addAttribute("author", author);
        return "author";
    }
    @GetMapping("/author/search")
    public String searchAuthors(@RequestParam("query") String query, Model model) {
        List<Author> authors = authorService.findByNameOrSurname(query);
        model.addAttribute("authors", authors);
        return "authors"; // Nome del template HTML
    }


    


}
