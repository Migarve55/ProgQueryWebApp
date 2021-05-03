package es.uniovi.controllers.rest;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public abstract class BaseRestController {
	
	protected String toErrorList(BindingResult result) {
		StringBuilder sb = new StringBuilder();
		List<ObjectError> errors = result.getAllErrors();
		if (!errors.isEmpty())
			sb.append(errors.get(0).getCode());
		for (int i = 1;i < errors.size();i++) {
			sb.append(", ");
			sb.append(errors.get(i).getObjectName());
		}
		return sb.toString();
	}

}
