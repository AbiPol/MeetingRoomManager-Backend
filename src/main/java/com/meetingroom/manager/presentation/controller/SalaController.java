package com.meetingroom.manager.presentation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meetingroom.manager.persistence.entity.EstadoSala;
import com.meetingroom.manager.persistence.entity.Sala;
import com.meetingroom.manager.services.exception.BadRequestException;
import com.meetingroom.manager.services.exception.DataNotFoundException;
import com.meetingroom.manager.services.interfaces.ISalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(path = "/api/v1/sala")
@Log4j2
@CrossOrigin(origins = "*", methods = { RequestMethod.POST,RequestMethod.DELETE,RequestMethod.GET, RequestMethod.PUT})
@PreAuthorize("hasAuthority('SALA')")
public class SalaController {
	//Objeto mapper para mapear log de salida y legibilidad del mismo
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	ISalaService salaService;

	@SuppressWarnings("null")
	@PostMapping(path = "/new")
	public ResponseEntity<?> nuevaSala(@RequestBody Sala newSala) throws JsonProcessingException{
		log.info("**[MeetingRoom]--- Estamos dando de alta una nueva sala en el sistema");
		
		try {
			newSala.setEstado(EstadoSala.DISPONIBLE);
			Sala salaAux = salaService.generoSala(newSala);
			String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(salaAux);
			log.info("**[MeetingRoom]--- La Sala a crear es: " + prettyJsonString);
			return new ResponseEntity<Sala>(salaAux,HttpStatus.CREATED);
			
		} catch (DataAccessException e) {
			String msgError = "";
			if(e instanceof DataIntegrityViolationException){
				log.info("**[MeetingRoom]--- Excepcion en new Sala: " + e.getMessage().split("Detail:")[1]);  
				msgError = e.getRootCause().getLocalizedMessage().split("Detail: ")[1];
			}else {
				log.info("**[MeetingRoom]--- Excepcion en new Sala: " + e.getMessage());
				msgError = e.getMessage();
			}
			throw  new BadRequestException("SAL-001",msgError);
		}
	}
	
	@PutMapping(path = "/updt")
	public ResponseEntity<?> updSala(@RequestBody Sala updSala) throws JsonProcessingException{
		log.info("**[MeetingRoom]--- Estamos creando un rol nuevo");
		
		//Transformamos el objeto JSON en un string para mostrarlo por consola.
		String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(updSala);
		log.info("**[MeetingRoom]--- Rol: " + prettyJsonString);
		try {
			Sala salaAux = salaService.buscoSalaId(updSala.getIdSala())
					  .orElseThrow(()-> new DataNotFoundException("SAL-002", "No se encuentra el Sala con id: " + updSala.getIdSala() + " dada de alta en el sistema"));
			
			if(!updSala.getNombreSala().equals(salaAux.getNombreSala())) {
				salaAux.setNombreSala((updSala.getNombreSala()));
				//System.out.println	("entramos en cambio de nombre");
			}
			
			if(updSala.getCapacidad() != salaAux.getCapacidad()) {
				salaAux.setCapacidad(updSala.getCapacidad());
				//System.out.println("entramos en cambio de descripcion");
			}
			
			if(!updSala.getUbicacion().equals(salaAux.getUbicacion())) {
				salaAux.setUbicacion(updSala.getUbicacion());
				//System.out.println("entramos en cambio de descripcion");
			}
			
			if(!updSala.getDescripcion().equals(salaAux.getDescripcion())) {
				salaAux.setDescripcion(updSala.getDescripcion());
				//System.out.println("entramos en cambio de descripcion");
			}
			
			return new ResponseEntity<Sala>(salaService.generoSala(salaAux), HttpStatus.CREATED);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("SAL-003",e.getMessage());
		}
	}
	
	@DeleteMapping(path = "/delete/{idSala}")
	public ResponseEntity<?> borroSala(@PathVariable("idSala") int id) throws JsonProcessingException{
		Map<String, Object> response = new HashMap<>();
		log.info("**[MeetingRoom]--- Estamos intentando dar de baja una sala de la BBDD");
		
		try {
			Sala salaAux = salaService.buscoSalaId(id)
					.orElseThrow(() -> new DataNotFoundException("SAL-004", "No se encuentra la sala con id: " + id + " dada de alta en el sistema"));
			
			String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(salaAux);
			log.info("**[MeetingRoom]--- La sala a dar de baja es: " + prettyJsonString);
			
			salaService.borroSala(salaAux);
			response.put("Mensaje","Sala borrada correctamente");
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("SAL-005",e.getMessage());
		}
	}
	
	@GetMapping(path = "/find/{nombre}")
	public ResponseEntity<?> buscoSalaNombre(@NonNull @PathVariable("nombre") String nom) throws JsonProcessingException {
		log.info("**[MeetingRoom]--- Estamos buscando una Sala por nombre");
		
		Sala salaAux = salaService.buscoSalaNombre(nom)
				.orElseThrow(() -> new DataNotFoundException("SAL-006", "El nombre de la sala '" + nom + "' no se encuentra dada de alta en el sistema"));
		
		String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(salaAux);
		log.info("**[MeetingRoom]--- Se busca este equipo: " + prettyJsonString);
		
		return new ResponseEntity<Sala>(salaAux, HttpStatus.OK);
	}
	
	@GetMapping(path = "/")
	public ResponseEntity<?> allSalas(){
		log.info("**[MeetingRoom]--- Estamos sacando de BBDD todas las salas existentes en BBDD" );
		
		try {
			List<Sala> salasAux = salaService.buscoAllSalas()
				.orElseThrow(() ->	new DataNotFoundException("SAL-007", "No hay ninguna sala dada de alta en el sistema"));
			
			return new ResponseEntity<List<Sala>>(salasAux,HttpStatus.OK);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("SAL-008",e.getMessage());
		}
	}
}
