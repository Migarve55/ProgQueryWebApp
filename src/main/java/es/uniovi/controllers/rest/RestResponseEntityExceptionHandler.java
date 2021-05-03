package es.uniovi.controllers.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { ResponseStatusException.class })
	protected ResponseEntity<Object> handleConflict(RuntimeException re, WebRequest request) {
		ResponseStatusException e = (ResponseStatusException) re;
		return handleExceptionInternal(re, e.getReason(), new HttpHeaders(), e.getStatus(), request);
	}

}