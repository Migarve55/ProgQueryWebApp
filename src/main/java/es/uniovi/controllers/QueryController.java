package es.uniovi.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

	@RequestMapping(value = "/query/add", method = RequestMethod.GET)
	public String addGet(Model model, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		List<User> users = usersService.getUsers();
		users.remove(user);
		model.addAttribute("usersList", users);
		model.addAttribute("query", new Query());
		return "query/add";
	}
	
	@RequestMapping(value = "/query/add", method = RequestMethod.POST)
	public String addPost(@Validated Query query, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		query.setUser(user);
		addQueryValidator.validate(query, result);
		if (result.hasErrors()) {
			return "query/add";
		}
		//Add it
		queryService.saveQuery(query);
		return "redirect:/query/list";
	}
	
	@RequestMapping("/query/list")
	public String list(Model model, Principal principal, Pageable pageable, @RequestParam(value = "", required = false) String searchText) {
		Page<Query> queries = null;
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (searchText != null && !searchText.isEmpty())
			queries = queryService.getQueriesFromUser(pageable, user, searchText);
		else
			queries = queryService.getQueriesFromUser(pageable, user);
		model.addAttribute("queriesList", queries.getContent());
		model.addAttribute("page", queries);
		return "query/list";
	}
	
	@RequestMapping("/query/detail/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal) {
		Query query = queryService.findQuery(id);
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (query == null) {
			return "redirect:/";
		}
		if (!queryService.canSeeQuery(user, query)) {
			return "redirect:/";
		}
		//Display the query
		model.addAttribute("query", query);
		return "query/detail";
	}
	
	@RequestMapping(value = "/query/edit/{id}", method = RequestMethod.GET)
	public String editGet(Model model, Principal principal, @PathVariable Long id) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		Query query = queryService.findQuery(id);
		if (query == null) {
			return "redirect:/";
		}
		if (!queryService.canModifyQuery(user, query)) {
			return "redirect:/query/detail/" + id;
		}
		model.addAttribute("query", query);
		return "query/edit";
	}
	
	@RequestMapping(value = "/query/edit/{id}", method = RequestMethod.POST)
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
		if (!queryService.canModifyQuery(user, original)) {
			return "redirect:/query/detail/" + id;
		}
		//Finally save
		original.setDescription(query.getDescription());
		original.setQueryText(query.getQueryText());
		original.setPublicForAll(query.isPublicForAll());
		queryService.saveQuery(original);
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
		if (!queryService.canModifyQuery(user, query)) {
			return "redirect:/query/list";
		}
		//Finally delete
		queryService.deleteQuery(query);
		return "redirect:/query/list";
	}
	
}
