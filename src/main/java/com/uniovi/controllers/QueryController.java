package com.uniovi.controllers;

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

import com.uniovi.entities.Query;
import com.uniovi.entities.User;
import com.uniovi.services.QueryService;
import com.uniovi.services.UsersService;
import com.uniovi.validators.AddQueryValidator;

@Controller
public class QueryController {
	
	@Autowired
	private QueryService queryService;
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private AddQueryValidator addQueryValidator;

	@RequestMapping(value = "/query/add", method = RequestMethod.GET)
	public String addGet(Model model, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		List<User> users = usersService.getUsers();
		users.remove(user);
		model.addAttribute("usersList", users);
		model.addAttribute("query", new Query());
		return "/query/add";
	}
	
	@RequestMapping(value = "/query/add", method = RequestMethod.POST)
	public String addPost(@Validated Query query, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		query.setUser(user);
		addQueryValidator.validate(query, result);
		if (result.hasErrors()) {
			return "/query/add";
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
		return "/query/list";
	}
	
	@RequestMapping("/query/detail/{id}")
	public String detail(Model model, @PathVariable Long id, Principal principal) {
		Query query = queryService.findQuery(id);
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		if (!canSeeQuery(user, query)) {
			return "redirect:/";
		}
		//Display the query
		model.addAttribute("query", query);
		return "/query/detail";
	}
	
	@RequestMapping(value = "/query/edit/{id}", method = RequestMethod.GET)
	public String editGet(Model model, @PathVariable Long id) {
		Query query = queryService.findQuery(id);
		if (query == null) {
			return "redirect:/";
		}
		model.addAttribute("query", query);
		return "/query/edit";
	}
	
	@RequestMapping(value = "/query/edit/{id}", method = RequestMethod.POST)
	public String editPost(@Validated Query query, @PathVariable Long id, BindingResult result, Principal principal) {
		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		addQueryValidator.validate(query, result);
		Query original = queryService.findQuery(id);
		if (result.hasErrors()) {
			return "/query/edit/" + query.getId();
		}
		if (original == null) {
			return "redirect:/";
		}
		if (!canModifyQuery(user, original)) {
			return "redirect:/";
		}
		//Finally save
		original.setName(query.getName());
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
		if (!canModifyQuery(user, query)) {
			return "redirect:/query/list";
		}
		//Finally delete
		queryService.deleteQuery(query);
		return "redirect:/query/list";
	}
	
	private boolean canSeeQuery(User user, Query query) {
		if (!query.isPublicForAll()) {
			if (!(query.getPublicTo().contains(user) || query.getUser().equals(user))) {
				return false;
			}
		}
		return true;
	}
	
	private boolean canModifyQuery(User user, Query query) {
		if (!canSeeQuery(user, query)) {
			return false;
		}
		if (!query.getUser().equals(user)) {
			return false;
		}
		return true;
	}
	
}
