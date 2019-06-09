package es.uniovi.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import es.uniovi.entities.Query;

@Component
public class EditQueryValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Query.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		//Query query = (Query) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "queryText", "error.empty");
	}

}