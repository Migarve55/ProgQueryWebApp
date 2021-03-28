package es.uniovi.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uniovi.entities.Query;
import es.uniovi.entities.User;
import es.uniovi.services.QueryService;
import es.uniovi.services.UsersService;
import es.uniovi.validators.AddQueryValidator;
import es.uniovi.validators.EditQueryValidator;

@Controller
public class QueryController {
	
	@Autowired
	private QueryService queryService;
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private AddQueryValidator addQueryValidator;
	
	@Autowired
	private EditQueryValidator editQueryValidator;

	@GetMapping("/query/add")
	public String addGet(Model model, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		List<User> users = usersService.getUsers();
		users.remove(user);
		model.addAttribute("usersList", users);
		model.addAttribute("query", new Query());
		return "query/add";
	}
	
	@PostMapping("/query/add")
	public String addPost(@Validated Query query, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		query.setUser(user);
		addQueryValidator.validate(query, result);
		if (result.hasErrors()) {
			return "query/add";
		}
		//Add it
		queryService.saveQuery(user, query);
		return "redirect:/query/list";
	}
	
	@GetMapping("/query/list")
	public String list(Model model, Principal principal, Pageable pageable, @RequestParam(value = "searchText", required = false) String searchText
			, @RequestParam(value = "onlyOwner", required = false) String onlyOwner) {
		Page<Query> queries = null;
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		queries = queryService.getQueriesFromUser(pageable, user, searchText, onlyOwner != null);
		model.addAttribute("searchText", searchText);
		model.addAttribute("onlyOwner", onlyOwner != null);
		model.addAttribute("queriesList", queries.getContent());
		model.addAttribute("page", queries);
		return "query/list";
	}
	
	@GetMapping("/query/detail/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal) {
		Query query = queryService.findQuery(id);
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (query == null) {
			return "redirect:/";
		}
		queryService.canSeeQuery(user, query);
		//Display the query
		model.addAttribute("canModify", queryService.canModifyQuery(user, query));
		model.addAttribute("query", query);
		return "query/detail";
	}
	
	@PostMapping("/query/addUser/{id}")
	public String addUser(Model model, @PathVariable Long id, HttpServletRequest request, Principal principal, RedirectAttributes redirect) {
		Query query = queryService.findQuery(id);
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		User toAdd = usersService.getUserByEmail(request.getParameter("user"));
		if (query == null) {
			return "redirect:/";
		}
		// Change query
		if (!queryService.addUser(user, query, toAdd)) {
			redirect.addFlashAttribute("error", "error.addUser");
			return "redirect:/query/detail/" + id;
		}
		return "redirect:/query/detail/" + id;
	}
	
	@PostMapping("/query/{id}/removeUser/{userId}")
	public String removeUser(Model model, @PathVariable Long id, @PathVariable Long userId, Principal principal, 
			RedirectAttributes redirect) {
		Query query = queryService.findQuery(id);
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		User toRemove = usersService.getUser(userId);
		if (query == null) {
			return "redirect:/";
		}
		// Change query
		if (!queryService.removeUser(user, query, toRemove)) {
			redirect.addFlashAttribute("error", "error.removeUser");
			return "redirect:/query/detail/" + id;
		}
		return "redirect:/query/detail/" + id;
	}
	
	@GetMapping("/query/edit/{id}")
	public String editGet(Model model, Principal principal, @PathVariable Long id) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Query query = queryService.findQuery(id);
		if (query == null) {
			return "redirect:/";
		}
		queryService.canModifyQuery(user, query);
		model.addAttribute("query", query);
		return "query/edit";
	}
	
	@PostMapping("/query/edit/{id}")
	public String editPost(@Validated Query query, @PathVariable Long id, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		editQueryValidator.validate(query, result);
		Query original = queryService.findQuery(id);
		if (result.hasErrors()) {
			return "/query/edit";
		}
		if (original == null) {
			return "redirect:/";
		}
		//Finally save
		original.setDescription(query.getDescription());
		original.setQueryText(query.getQueryText());
		original.setPublicForAll(query.isPublicForAll());
		queryService.saveQuery(user, original);
		return "redirect:/query/detail/" + id;
	}
	
	@RequestMapping("/query/delete/{id}")
	public String delete(@PathVariable Long id, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Query query = queryService.findQuery(id);
		if (query == null) {
			return "redirect:/query/list";
		}
		//Finally delete
		queryService.deleteQuery(user, query);
		return "redirect:/query/list";
	}
	
	@ResponseBody
	@RequestMapping("/query/search")
	public List<Query> getAutoCompleteQueryList(Principal principal, @RequestParam(value = "q") String searchText) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		return queryService.findAvailableQueriesForUserByName(user, searchText, 10);
	}
	
}
