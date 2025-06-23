package com.knowy.server.controller;

import com.knowy.server.entity.UserConfiguration;
import com.knowy.server.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserConfigController {

	private UserService userService;

	public UserConfigController(UserService userService) {
		this.userService = userService;
	}


	String username = "usuario123";
	//User-Profile
	@GetMapping("/user-profile")
	public String viewUserProfile(Model model) {
		model.addAttribute("username", username);
		return "pages/user-management/user-profile";
	}

	//User-Account
	@GetMapping("/user-account")
	public String viewUserAccount(Model model) {
		UserConfiguration user = userService.getCurrentUser("usuario123@correo.com");
		model.addAttribute("user", user);
		return "pages/user-management/user-account";
	}
	@PostMapping("/update-privateUsername")
	public String updatePrivateUsername(@RequestParam String newPrivateUsername, @RequestParam String email, RedirectAttributes redirectAttributes) {
		if(userService.updatePrivateUsername(email, newPrivateUsername)){
			redirectAttributes.addFlashAttribute("success", "Nombre privado actualizado");
		}else{
			redirectAttributes.addFlashAttribute("error", "Nombre no valido");
		}
		return "redirect:/user-account";
	}

	@PostMapping("/update-email")
	public String updateEmail(@RequestParam String username, @RequestParam String newEmail, @RequestParam String currentPassword, RedirectAttributes redirectAttributes){
		if(userService.updateEmail(username, newEmail, currentPassword)){
			redirectAttributes.addFlashAttribute("successEmail", "Email actualizado");
		}else{
			redirectAttributes.addFlashAttribute("error", "Algo salió mal");
		}
		return "redirect:/user-account";
	}

	// Delete account
	@GetMapping("/delete-account")
	public String deleteAccountForm(ModelMap interfaceScreen) {
		interfaceScreen.addAttribute("username", username);
		return "pages/user-management/delete-account";
	}

	//Delete-Account-End (Finally deleting Account)
	@GetMapping ("/delete-account-end")
	public String deleteAccountEnd(ModelMap interfaceScreen) {
		interfaceScreen.addAttribute("username", username);
		return "pages/user-management/delete-account-end";
	}
}
