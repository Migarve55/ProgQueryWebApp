package es.uniovi.controllers;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeControllers {

	@RequestMapping("/")
	public String index() {
		return "index";
	}
	
	@RequestMapping("help")
	public String help() {
		Locale locale = LocaleContextHolder.getLocale();
		return "help/" + locale.getLanguage();
	}

}
