package es.uniovi.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import es.uniovi.entities.User;

@Component
public class EditUserValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.empty");
		if (user.getName().length() > User.NAME_LENGTH) {
			errors.rejectValue("name", "error.signup.name.length");
		}
		if (user.getLastName().length() > User.LASTNAME_LENGTH) {
			errors.rejectValue("lastName", "error.signup.lastName.length");
		}
	}

}
