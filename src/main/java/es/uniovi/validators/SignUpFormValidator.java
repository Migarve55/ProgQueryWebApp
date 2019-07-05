package es.uniovi.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import es.uniovi.entities.User;
import es.uniovi.services.UsersService;

@Component
public class SignUpFormValidator implements Validator {

	private final static String EMAIL_REGEX = ".+@.+\\..+";
	
	@Autowired
	private UsersService usersService;
	
	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "error.empty");
		if (!user.getEmail().matches(EMAIL_REGEX)) {
			errors.rejectValue("email", "error.signup.email.regex");
		}
		if (usersService.getUserByEmail(user.getEmail()) != null) {
			errors.rejectValue("email", "error.signup.email.duplicate");
		}
		if (user.getEmail().length() > 50) {
			errors.rejectValue("name", "error.signup.email.length");
		}
		if (user.getName().length() < 5 || user.getName().length() > 24) {
			errors.rejectValue("name", "error.signup.name.length");
		}
		if (user.getLastName().length() < 5 || user.getLastName().length() > 24) {
			errors.rejectValue("lastName", "error.signup.lastName.length");
		}
		if (usersService.validateUserPassword(user.getPassword())) {
			errors.rejectValue("password", "error.password");
		}
		if (!user.getPasswordConfirm().equals(user.getPassword())) {
			errors.rejectValue("passwordConfirm", "error.password.coincidence");
		}
	}

}
