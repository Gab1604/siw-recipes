package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ReviewService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

	private final UserService userService;
	private final BookService bookService;
	private final ReviewService reviewService;

	public AdminController(UserService userService, BookService bookService, ReviewService reviewService) {
		this.userService = userService;
		this.bookService = bookService;
		this.reviewService = reviewService;
	}

	@GetMapping
	public String dashboard(Model model) {
		model.addAttribute("totalUsers", userService.findAll().size());
		model.addAttribute("totalBooks", bookService.findAll().size());
		model.addAttribute("totalReviews", reviewService.findAll().size());
		model.addAttribute("recentBooks", bookService.findAll().stream().limit(5).toList());
		return "admin/index";
	}

	@GetMapping("/users")
	public String listUsers(Model model) {
		model.addAttribute("users", userService.findAll());
		return "admin/users";
	}

	@GetMapping("/books")
	public String listBooks(Model model) {
		model.addAttribute("books", bookService.findAll());
		return "admin/books";
	}

	@GetMapping("/reviews")
	public String listReviews(Model model) {
		model.addAttribute("reviews", reviewService.findAll());
		return "admin/reviews";
	}

	@PostMapping("/reviews/delete/{id}")
	public String deleteReview(@PathVariable Long id) {
		reviewService.deleteById(id);
		return "redirect:/admin/reviews";
	}
}
