package es.uniovi.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import es.uniovi.entities.Query;
import es.uniovi.services.QueryService;

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
		if (query.getName() == null || query.getDescription() == null || query.getQueryText() == null)
			return;
		if (queryService.findQueryByName(query.getName()) != null) {
			errors.rejectValue("name", "error.query.name.duplicate");
		}
		if (query.getName().length() > Query.NAME_LENGTH) {
			errors.rejectValue("name", "error.query.name.length");
		}
		if (!queryService.validateQueryName(query.getName())) {
			errors.rejectValue("name", "error.query.name.regex");
		}
		if (query.getDescription().length() > Query.DESCRIPTION_LENGTH) {
			errors.rejectValue("description", "error.query.description.length");
		}
		if (query.getQueryText().length() > Query.QUERY_LENGTH) {
			errors.rejectValue("queryText", "error.query.length");
		}
	}

}
