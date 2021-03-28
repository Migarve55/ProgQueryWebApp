package es.uniovi.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uniovi.analyzer.tools.reporter.Neo4jFacade;
import es.uniovi.entities.Program;
import es.uniovi.entities.User;
import es.uniovi.repositories.ProgramRepository;

@Service
public class ProgramService {
	
	private final static String NAME_REGEX = "[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+";

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ProgramRepository programRepository;
	
	public Program findProgram(Long id) {
		return programRepository.findById(id).orElse(null);
	}
	
	public Program findProgramByName(String name) {
		return programRepository.findByName(name).orElse(null);
	}
	
	public void addProgram(Program program) {
		programRepository.save(program);
	}
	
	public Page<Program> listByUser(Pageable pageable, User user) {
		return programRepository.findAllByUser(pageable, user);
	}
	
	public List<Program> listByUser(User user) {
		return programRepository.findAllByUser(user);
	}
	
	public Program getLastFromUser(User user) {
		List<Program> results = programRepository.findLastByUser(user);
		return results.isEmpty() ? null : results.get(0);
	}
	
	@Transactional
	public void deleteProgram(Long id) {
		try (Neo4jFacade neo4jFacade = new Neo4jFacade(System.getProperty("neo4j.url"))) {
			Program program = programRepository.findById(id).orElse(null);
			if (program.getProgramIdentifier() != null)
				neo4jFacade.removeProgram(program.getProgramIdentifier());
			programRepository.deleteById(id);
			logger.info("Program {} was deleted", program.getProgramIdentifier());
		}
	}
	
	public boolean validateProgramName(String name) {
		return name.matches(NAME_REGEX);
	}
	
	public boolean isProgramNameDuplicated(String name) {
		return programRepository.findByName(name).isPresent();
	}

	public List<Program> findAvailableProgramsForUserByName(User user, String searchText, int limit) {
		searchText = "%" + searchText + "%";
		return programRepository.findAvailableProgramsForUserByName(user, searchText, limit);
	}
	
}
