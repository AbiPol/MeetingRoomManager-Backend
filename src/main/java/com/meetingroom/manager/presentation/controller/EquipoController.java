package com.meetingroom.manager.presentation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meetingroom.manager.persistence.entity.Equipo;
import com.meetingroom.manager.services.exception.BadRequestException;
import com.meetingroom.manager.services.exception.DataNotFoundException;
import com.meetingroom.manager.services.interfaces.IEquipoService;
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

//import jakarta.annotation.security.DenyAll;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(path = "/api/v1/equipo")
@Log4j2
@CrossOrigin(origins = "*", methods = { RequestMethod.POST,RequestMethod.DELETE,RequestMethod.GET, RequestMethod.PUT})
@PreAuthorize("hasAuthority('EQUIPO')")
public class EquipoController {

	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	IEquipoService equipoService;
	
	@SuppressWarnings("null")
	@PostMapping(path = "/new")
	public ResponseEntity<?> nuevoEquipo(@RequestBody Equipo newEquipo) throws JsonProcessingException{
		log.info("**[MeetingRoom]--- Estamos dando de alta un equipo nuevo en el sistema");
		
		try {
			Equipo equipoAux = equipoService.generoEquipo(newEquipo);
			String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(equipoAux);
			log.info("**[MeetingRoom]--- El equipo a crear es: " + prettyJsonString);
			return new ResponseEntity<Equipo>(equipoAux,HttpStatus.CREATED);
			
		} catch (DataAccessException e) {
			String msgError = "";
			//System.out.println("menasaje Error:" + e.getRootCause().getLocalizedMessage().split("Detail: ")[1]);
			if(e instanceof DataIntegrityViolationException){
				//log.info("Excepcion en new User: " + e.getMessage().split("Detail:")[1]);  
				msgError = e.getRootCause().getLocalizedMessage().split("Detail: ")[1];
			}else {
				//log.info("Excepcion en new User: " + e.getMessage());
				msgError = e.getMessage();
			}
			throw  new BadRequestException("EQP-001",msgError);
		}/* catch (ConstraintViolationException ex) {
			String mensaje = "";
			for(ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
		         //mensaje = "El campo '" + constraintViolation.getPropertyPath() + "' : " + constraintViolation.getMessage();
				mensaje = mensaje + "El campo 'Denominacion Equipo' " + constraintViolation.getMessage();
		    }   
			throw new EmptyRequestException(mensaje);
		}*/
	}
	
	@PutMapping(path = "/updt")
	public ResponseEntity<?> updRol(@RequestBody Equipo updEquipo) throws JsonProcessingException{
		log.info("**[MeetingRoom]--- Estamos creando un rol nuevo");
		
		//Transformamos el objeto JSON en un string para mostrarlo por consola.
		String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(updEquipo);
		log.info("**[MeetingRoom]--- Rol: " + prettyJsonString);
		try {
			Equipo equipoAux = equipoService.buscoEqupoId(updEquipo.getIdEquipo())
					  .orElseThrow(()-> new DataNotFoundException("EQP-004", "No se encuentra el Equipo con id: " + updEquipo.getIdEquipo() + " dado de alta en el sistema"));
			
			if(!updEquipo.getNombreEquipo().equals(equipoAux.getNombreEquipo())) {
				equipoAux.setNombreEquipo(updEquipo.getNombreEquipo());
				//System.out.println("entramos en cambio de nombre");
			}
			
			if(!updEquipo.getDescripcionEquipo().equals(equipoAux.getDescripcionEquipo())) {
				equipoAux.setDescripcionEquipo(updEquipo.getDescripcionEquipo());
				//System.out.println("entramos en cambio de descripcion");
			}
			
			return new ResponseEntity<Equipo>(equipoService.generoEquipo(equipoAux), HttpStatus.CREATED);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("EQP-005",e.getMessage());
		}
	}
	
	@DeleteMapping(path = "/delete/{idEquipo}")
	public ResponseEntity<?> borroEquipo(@PathVariable("idEquipo") int id) throws JsonProcessingException{
		Map<String, Object> response = new HashMap<>();
		log.info("**[MeetingRoom]--- Estamos intentando dar de baja un equipo de la BBDD");
		
		try {
			Equipo equipoAux = equipoService.buscoEqupoId(id)
					.orElseThrow(() -> new DataNotFoundException("EQP-002", "No se encuentra el equipo con id: " + id + " dado de alta en el sistema"));
			
			String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(equipoAux);
			log.info("**[MeetingRoom]--- El equipo a dar de baja es: " + prettyJsonString);
			
			equipoService.borroEquipo(equipoAux);
			response.put("Mensaje","Equipo borrado correctamente");
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("EQP-003",e.getMessage());
		}
	}
	
	@GetMapping(path = "/find/{nombre}")
	public ResponseEntity<?> buscoEqpNombre(@NonNull @PathVariable("nombre") String nom) throws JsonProcessingException {
		log.info("**[MeetingRoom]--- Estamos intentando dar de baja un equipo de la BBDD");
		
		Equipo equipoAux = equipoService.buscoEquipoNombre(nom)
				.orElseThrow(() -> new DataNotFoundException("EQP-004", "El nombre del equipo '" + nom + "' no se encuentra dado de alta en el sistema"));
		
		String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(equipoAux);
		log.info("**[MeetingRoom]--- Se busca este equipo: " + prettyJsonString);
		
		return new ResponseEntity<Equipo>(equipoAux, HttpStatus.OK);
	}
	
	@GetMapping(path = "/")
	public ResponseEntity<?> todosRoles(){
		log.info("**[MeetingRoom]--- Estamos sacando de BBDD todos el equipamiento existente" );
		
		try {
			List<Equipo> equiposAux = equipoService.buscoAllEquipos()
				.orElseThrow(() ->	new DataNotFoundException("EQP-005", "No hay ningun equipo dado de alta en el sistema"));
			
			return new ResponseEntity<List<Equipo>>(equiposAux,HttpStatus.OK);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("EQP-006",e.getMessage());
		}
	}
}
