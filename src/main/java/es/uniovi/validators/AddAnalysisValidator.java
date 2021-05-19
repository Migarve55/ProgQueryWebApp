package es.uniovi.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import es.uniovi.entities.Analysis;
import es.uniovi.services.AnalysisService;

@Component
public class AddAnalysisValidator implements Validator {
	
	@Autowired
	private AnalysisService analysisService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Analysis.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Analysis analysis = (Analysis) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "queryText", "error.empty");
		if (analysis.getName() == null || analysis.getDescription() == null || analysis.getQueryText() == null)
			return;
		if (analysisService.findAnalysisByName(analysis.getName()) != null) {
			errors.rejectValue("name", "error.query.name.duplicate");
		}
		if (analysis.getName().length() > Analysis.NAME_LENGTH) {
			errors.rejectValue("name", "error.query.name.length");
		}
		if (!analysisService.validateAnalysisName(analysis.getName())) {
			errors.rejectValue("name", "error.query.name.regex");
		}
		if (analysis.getDescription().length() > Analysis.DESCRIPTION_LENGTH) {
			errors.rejectValue("description", "error.query.description.length");
		}
		if (analysis.getQueryText().length() > Analysis.QUERY_LENGTH) {
			errors.rejectValue("queryText", "error.query.length");
		}
		String querySyntaxError = analysisService.checkAnalysisSyntax(analysis.getQueryText());
		if (querySyntaxError != null) {
			errors.rejectValue("queryText", "queryText$$error", querySyntaxError);
		}
	}

}
