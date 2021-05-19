package es.uniovi.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uniovi.controllers.exceptions.ForbiddenException;
import es.uniovi.entities.Analysis;
import es.uniovi.entities.User;
import es.uniovi.reflection.codeanalysis.cypheradapter.processing.CypherAdapter;
import es.uniovi.repositories.ProblemsRepository;
import es.uniovi.repositories.AnalysisRepository;

@Service
public class AnalysisService {
	
	private final static String NAME_REGEX = "\\w+\\.\\w+\\.\\w+(\\.\\w+)*";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AnalysisRepository analysisService;
	
	@Autowired
	private ProblemsRepository problemsRepository;
	
	public Analysis findAnalysis(Long id) {
		return analysisService.findById(id).orElse(null);
	}
	
	public Analysis findAnalysisByName(String name) {
		return analysisService.findByName(name);
	}
	
	public List<Analysis> getAnalysisFromUser(User user) {
		return analysisService.findAllByUserOrderByName(user);
	}

	public Page<Analysis> getAnalysisFromUser(Pageable pageable, User user) {
		return analysisService.findAllByUserOrderByName(pageable, user);
	}
	
	public Page<Analysis> getAnalysisFromUser(Pageable pageable, User user, String searchText, boolean onlyOwner) {
		if (searchText == null)
			searchText = "%";
		else
			searchText = "%" + searchText + "%";
		if (onlyOwner)
			return analysisService.findAllByUserAndNameLike(pageable, user, searchText);
		else
			return analysisService.findAvailableForUser(pageable, user, searchText);
	}
	
	public List<Analysis> findAvailableAnalysesForUserByName(User user, String searchText, int limit) {
		searchText = "%" + searchText + "%";
		return analysisService.findAvailableForUserByName(user, searchText, limit);
	}
	
	public List<Analysis> getPublicAnalyses() {
		return analysisService.findAllPublic();
	}
	
	public List<Analysis> getAvailableAnalysesForUser(User user) {
		return analysisService.findAvailableForUser(user);
	}
	
	public Optional<Analysis> getAnalysesFromUserByName(User user, String analysisName) {
		return getAvailableAnalysesForUser(user).stream()
				.filter(q -> q.getName().equals(analysisName))
				.findFirst();
	}
	
	public void saveAnalysis(User user, Analysis analysis) {
		checkUserWriteAnalysisAccess(user, analysis);
		analysis.setModified(new Date());
		analysisService.save(analysis);
		logger.info("Analysis '{}' was saved", analysis.getName());
	}
	

	public boolean addUser(User user, Analysis analysis, User toAdd) {
		if (toAdd == null)
			return false;
		if (analysis.getPublicTo().contains(toAdd))
			return false;
		checkUserWriteAnalysisAccess(user, analysis);
		analysis.getPublicTo().add(toAdd);
		analysisService.save(analysis);
		logger.info("Analysis '{}' is now visible to user '{}'", analysis.getName(), toAdd.getEmail());
		return true;
	}

	public boolean removeUser(User user, Analysis analysis, User toRemove) {
		if (toRemove == null)
			return false;
		if (!analysis.getPublicTo().contains(toRemove))
			return false;
		checkUserWriteAnalysisAccess(user, analysis);
		analysis.getPublicTo().remove(toRemove);
		analysisService.save(analysis);
		logger.info("Analysis '{}' is no longer visible to user '{}'", analysis.getName(), toRemove.getEmail());
		return true;
	}
	
	@Transactional
	public void deleteAnalysis(User user, Analysis analysis) {
		checkUserWriteAnalysisAccess(user, analysis);
		analysisService.delete(analysis);
		problemsRepository.setAnalysisAsDeleted(analysis);
		logger.info("Analysis '{}' was deleted", analysis.getName());
	}
	
	// Auxiliar
	
	public void checkUserReadAnalysisAccess(User user, Analysis analysis) {
		if (!canSeeAnalysis(user, analysis)) {
			logger.warn("User '{}' does not have read access to analysis '{}'", user.getEmail(), analysis.getName());
			throw new ForbiddenException();
		}
	}
	
	public void checkUserWriteAnalysisAccess(User user, Analysis analysis) {
		if (!canModifyAnalysis(user, analysis)) {
			logger.warn("User '{}' does not have write access to analysis '{}'", user.getEmail(), analysis.getName());
			throw new ForbiddenException();
		}
	}
	
	public boolean canSeeAnalysis(User user, Analysis analysis) {
		if (!analysis.isPublicForAll()) {
			if (!(analysis.getPublicTo().contains(user) || analysis.getUser().equals(user))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean canModifyAnalysis(User user, Analysis analysis) {
		return analysis.getUser().equals(user);
	}
	
	public String checkAnalysisSyntax(String query) {
		try {
			CypherAdapter.limitQuery(query, new String[0], "");
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	public void deleteAll() {
		analysisService.deleteAll();
	}
	
	public boolean validateAnalysisName(String name) {
		return name.matches(NAME_REGEX);
	}
	
}
