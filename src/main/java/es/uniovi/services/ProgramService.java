package es.uniovi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.uniovi.entities.Program;
import es.uniovi.entities.User;
import es.uniovi.repositories.ProgramRepository;

@Service
public class ProgramService {

	@Autowired
	private ProgramRepository programRepository;
	
	public Program getProgram(Long id) {
		return programRepository.findById(id).orElse(null);
	}
	
	public void addProgram(Program program) {
		programRepository.save(program);
	}
	
	public Page<Program> listByUser(Pageable pageable, User user) {
		return programRepository.findAllByUser(pageable, user);
	}
	
	public void deleteProgram(Long id) {
		programRepository.deleteById(id);
	}
	
}
