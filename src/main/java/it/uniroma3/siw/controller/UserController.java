package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final CredentialsService credentialsService;

	public UserController(UserService userService, CredentialsService credentialsService) {
		this.userService = userService;
		this.credentialsService = credentialsService;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile")
	public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User user = credentials.getUser();
		model.addAttribute("user", user);
		return "profile";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile/edit")
	public String editProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User user = credentials.getUser();
		model.addAttribute("user", user);
		return "profile_edit";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/profile/edit")
	public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
	                            @Valid @ModelAttribute("user") User updatedUser, BindingResult result) {
		if (result.hasErrors()) {
			return "profile_edit";
		}
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User user = credentials.getUser();
		user.setFirstName(updatedUser.getFirstName());
		user.setLastName(updatedUser.getLastName());
		userService.saveUser(user);
		return "redirect:/user/profile";
	}
}
