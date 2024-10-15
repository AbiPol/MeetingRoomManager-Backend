package com.meetingroom.manager.persistence.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sala", indexes = {@Index(columnList = "nombreSala")})
public class Sala implements Serializable {

	//public enum Estado {DISPONIBLE,NODISPONIBLE};
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idSala;
	
	@Column(nullable = false)
	private String nombreSala;
	
	@Column(nullable = false)
	private int capacidad;
	
	@Column(nullable = false)
	private String ubicacion;
	
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	/*
	 * Valor 1 = DISPONIBLE
	 * Valor 0 = NO DISPONIBLE
	 */
	private EstadoSala estado;
	
	private String descripcion;
	
	//Union con la entidad Equipo
	@OneToMany(mappedBy = "idSala")
	@JsonManagedReference(value = "equipos-sala")
	private List<Equipo> equipamiento;
	
	// Union con la entidad Reserva
	@OneToMany(mappedBy = "idSala")
	@JsonManagedReference(value = "sala-reserva")
	private List<Reserva> reservas;
	
	@Version
	@Column(name = "regVersion", columnDefinition = "bigint DEFAULT 0", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private long version;
	
	//Union con la entidad HorarioDisponibilidad de la sala
	@ManyToMany
	@JoinTable(name = "sala-Horario_disp", 
	           joinColumns = @JoinColumn(name = "FK_sala"), 
	           inverseJoinColumns = @JoinColumn(name = "FK_Horario_Disp")
	)
	private Set<HorarioDisponibilidad> HorarioDisp;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
