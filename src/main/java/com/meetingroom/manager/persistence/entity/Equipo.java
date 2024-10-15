package com.meetingroom.manager.persistence.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(indexes = {@Index(name = "nomequipo_inx",columnList = "nombreEquipo")})
public class Equipo implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idEquipo;
	
	@Column(nullable = false)
	@NotBlank
	private String nombreEquipo;
	
	private String descripcionEquipo;
	
	@ManyToOne
	@JoinColumn(name = "FK_sala")
	@JsonBackReference(value = "equipos-sala")
	private Sala idSala;
	
	
	@Version
	@Column(name = "regVersion", columnDefinition = "bigint DEFAULT 0", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private long version;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
