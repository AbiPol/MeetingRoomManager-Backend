package com.meetingroom.manager.services.implementacion;

import java.util.List;
import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Sala;
import com.meetingroom.manager.persistence.repository.ISalaDAO;
import com.meetingroom.manager.services.interfaces.ISalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;

@Service
public class SalaServiceImpl implements ISalaService {

	@Autowired
	ISalaDAO salaDao;
	
	@Override
	@Transactional
	public Sala generoSala(Sala newSala) {
		return salaDao.save(newSala);
	}

	@Override
	public void borroSala(Sala sala) {
		salaDao.delete(sala);
	}

	@Override
	public Optional<Sala> buscoSalaNombre(String nombre) {
		return salaDao.findByNombreSala(nombre);
	}

	@Override
	public Optional<Sala> buscoSalaId(int id) {
		return salaDao.findById(id);
	}

	@Override
	public Optional<List<Sala>> buscoAllSalas() {
		return Optional.ofNullable(salaDao.findAll());
	}

}
