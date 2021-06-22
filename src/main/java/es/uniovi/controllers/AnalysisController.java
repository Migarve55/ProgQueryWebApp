package es.uniovi.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uniovi.entities.Analysis;
import es.uniovi.entities.User;
import es.uniovi.services.AnalysisService;
import es.uniovi.services.UsersService;
import es.uniovi.validators.AddAnalysisValidator;
import es.uniovi.validators.EditAnalysisValidator;

@Controller
public class AnalysisController {
	
	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private AddAnalysisValidator addAnalysisValidator;
	
	@Autowired
	private EditAnalysisValidator editAnalysisValidator;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@GetMapping("/query/add")
	public String addGet(Model model, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		List<User> users = usersService.getUsers();
		users.remove(user);
		model.addAttribute("usersList", users);
		model.addAttribute("analysis", new Analysis());
		return "query/add";
	}
	
	@PostMapping("/query/add")
	public String addPost(Model model, @Validated Analysis analysis, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		analysis.setUser(user);
		addAnalysisValidator.validate(analysis, result);
		if (result.hasErrors()) {
			model.addAttribute("analysis", analysis);
			return "query/add";
		}
		//Add it
		analysisService.saveAnalysis(user, analysis);
		return "redirect:/query/list";
	}
	
	@GetMapping("/query/list")
	public String list(Model model, Principal principal, Pageable pageable, @RequestParam(value = "searchText", required = false) String searchText
			, @RequestParam(value = "onlyOwner", required = false) String onlyOwner) {
		Page<Analysis> queries = null;
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		queries = analysisService.getAnalysisFromUser(pageable, user, searchText, onlyOwner != null);
		model.addAttribute("searchText", searchText);
		model.addAttribute("onlyOwner", onlyOwner != null);
		model.addAttribute("queriesList", queries.getContent());
		model.addAttribute("page", queries);
		return "query/list";
	}
	
	@GetMapping("/query/detail/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal) {
		Analysis analysis = analysisService.findAnalysis(id);
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (analysis == null) {
			return "redirect:/";
		}
		analysisService.canSeeAnalysis(user, analysis);
		//Display the query
		model.addAttribute("canModify", analysisService.canModifyAnalysis(user, analysis));
		model.addAttribute("query", analysis);
		return "query/detail";
	}
	
	@PostMapping("/query/addUser/{id}")
	public String addUser(Model model, @PathVariable Long id, HttpServletRequest request, Principal principal, RedirectAttributes redirect) {
		Analysis analysis = analysisService.findAnalysis(id);
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		User toAdd = usersService.getUserByEmail(request.getParameter("user"));
		if (analysis == null) {
			return "redirect:/";
		}
		analysisService.canModifyAnalysis(user, analysis);
		// Change query
		if (!analysisService.addUser(user, analysis, toAdd)) {
			redirect.addFlashAttribute("error", "error.addUser");
			return "redirect:/query/detail/" + id;
		}
		return "redirect:/query/detail/" + id;
	}
	
	@PostMapping("/query/{id}/removeUser/{userId}")
	public String removeUser(Model model, @PathVariable Long id, @PathVariable Long userId, Principal principal, 
			RedirectAttributes redirect) {
		Analysis analysis = analysisService.findAnalysis(id);
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		User toRemove = usersService.getUser(userId);
		if (analysis == null) {
			return "redirect:/";
		}
		analysisService.canModifyAnalysis(user, analysis);
		// Change query
		if (!analysisService.removeUser(user, analysis, toRemove)) {
			redirect.addFlashAttribute("error", "error.removeUser");
			return "redirect:/query/detail/" + id;
		}
		return "redirect:/query/detail/" + id;
	}
	
	@GetMapping("/query/edit/{id}")
	public String editGet(Model model, Principal principal, @PathVariable Long id) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Analysis analysis = analysisService.findAnalysis(id);
		if (analysis == null) {
			return "redirect:/";
		}
		analysisService.canModifyAnalysis(user, analysis);
		model.addAttribute("analysis", analysis);
		return "query/edit";
	}
	
	@PostMapping("/query/edit/{id}")
	public String editPost(Model model, @Validated Analysis analysis, @PathVariable Long id, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		editAnalysisValidator.validate(analysis, result);
		Analysis original = analysisService.findAnalysis(id);
		analysisService.canModifyAnalysis(user, original);
		if (result.hasErrors()) {
			model.addAttribute("analysis", analysis);
			return "query/edit";
		}
		if (original == null) {
			return "redirect:/";
		}
		//Finally save
		original.setDescription(analysis.getDescription());
		original.setQueryText(analysis.getQueryText());
		original.setPublicForAll(analysis.isPublicForAll());
		analysisService.saveAnalysis(user, original);
		return "redirect:/query/detail/" + id;
	}
	
	@RequestMapping("/query/{id}/delete")
	public String delete(@PathVariable Long id, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Analysis analysis = analysisService.findAnalysis(id);
		if (analysis == null) {
			return "redirect:/query/list";
		}
		//Finally delete
		analysisService.deleteAnalysis(user, analysis);
		return "redirect:/query/list";
	}
	
	@ResponseBody
	@RequestMapping("/query/search")
	public List<Analysis> getAutoCompleteQueryList(Principal principal, @RequestParam(value = "q") String searchText) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		return analysisService.findAvailableAnalysesForUserByName(user, searchText, 10);
	}
	
}
