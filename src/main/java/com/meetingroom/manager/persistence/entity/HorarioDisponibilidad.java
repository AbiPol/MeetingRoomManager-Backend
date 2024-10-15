package com.meetingroom.manager.persistence.entity;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class HorarioDisponibilidad implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private Date fecha;
	
	private Time horaInicio;
	
	private Time horaFin;
	
	@ManyToMany(mappedBy = "HorarioDisp")
	private List<Sala> idSalas;
	
	@Version
	@Column(name = "regVersion", columnDefinition = "bigint DEFAULT 0", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private long version;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
