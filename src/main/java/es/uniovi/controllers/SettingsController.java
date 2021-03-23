package es.uniovi.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uniovi.entities.User;
import es.uniovi.services.UsersService;

@Controller
public class SettingsController {

	@Autowired
	private UsersService usersService;

	@RequestMapping("/settings")
	public String settings(Model model, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		model.addAttribute("user", user);
		return "settings";
	}

	@RequestMapping(value = "/settings/changePassword", method = RequestMethod.POST)
	public String changePassword(Principal principal, @RequestParam("actualPass") String actualPass,
			@RequestParam("newPass") String newPass, @RequestParam("newPassConf") String newPassConf,
			RedirectAttributes redir) {
		String em = principal.getName();
		User user = usersService.getUserByEmail(em);
		actualPass = actualPass.trim();
		newPass = newPass.trim();
		newPassConf = newPassConf.trim();
		// Confirm data
		if (!usersService.checkUserPassword(user, actualPass)) {
			redir.addFlashAttribute("passChangeError", "error.settings.password");
			return "redirect:/settings";
		}
		if (usersService.validateUserPassword(newPass)) {
			redir.addFlashAttribute("passChangeError", "error.password");
			return "redirect:/settings";
		}
		if (!newPass.equals(newPassConf)) {
			redir.addFlashAttribute("passChangeError", "error.settings.newPassword.coincidence");
			return "redirect:/settings";
		}
		// Change data
		usersService.changeUserPassword(user, newPass);
		return "redirect:/settings";
	}

	@RequestMapping(value = "/settings/deleteProfile", method = RequestMethod.POST)
	public String delete(Principal principal, @RequestParam String email, RedirectAttributes redir) {
		String em = principal.getName();
		User user = usersService.getUserByEmail(em);
		// Check the confirming data is ok
		if (!email.trim().equals(user.getEmail())) {
			redir.addFlashAttribute("profileDelError", "error.settings.email");
			return "redirect:/settings";
		}
		// Finally delete
		usersService.deleteUser(user.getId());
		return "redirect:/logout";
	}

}
