package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/books/{bookId}/review")
@PreAuthorize("hasAuthority('DEFAULT')")
public class ReviewController {

	private final ReviewService reviewService;
	private final BookService bookService;
	private final UserService userService;
	private final CredentialsService credentialsService;

	public ReviewController(ReviewService reviewService, BookService bookService,
	                        UserService userService, CredentialsService credentialsService) {
		this.reviewService = reviewService;
		this.bookService = bookService;
		this.userService = userService;
		this.credentialsService = credentialsService;
	}

	@GetMapping
	public String formReview(@PathVariable("bookId") Long bookId, Model model) {
		var bookOpt = bookService.findById(bookId);
		if (bookOpt.isEmpty()) {
			return "redirect:/books?error=notfound";
		}
		model.addAttribute("book", bookOpt.get());
		model.addAttribute("review", new Review());
		return "add-review";
	}

	@PostMapping
	public String submitReview(@PathVariable("bookId") Long bookId,
	                           @Valid @ModelAttribute("review") Review review,
	                           BindingResult bindingResult,
	                           Model model,
	                           Principal principal) {

		var bookOpt = bookService.findById(bookId);
		if (bookOpt.isEmpty()) {
			return "redirect:/books?error=notfound";
		}
		Book book = bookOpt.get();

		if (bindingResult.hasErrors()) {
			model.addAttribute("book", book);
			return "add-review";
		}

		// Recupera User da Credentials
		Credentials credentials = credentialsService.getCredentials(principal.getName());
		User user = credentials.getUser();

		review.setBook(book);
		review.setUser(user);
		reviewService.save(review);

		return "redirect:/books/" + bookId;
	}
}
