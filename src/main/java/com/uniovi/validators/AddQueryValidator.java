package com.uniovi.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.uniovi.entities.Query;
import com.uniovi.services.QueryService;

@Component
public class AddQueryValidator implements Validator {
	
	@Autowired
	private QueryService queryService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Query.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Query query = (Query) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "queryText", "error.empty");
		if (queryService.findQueryByName(query.getName()) != null) {
			errors.rejectValue("name", "error.query.name.duplicate");
		}
	}

}
