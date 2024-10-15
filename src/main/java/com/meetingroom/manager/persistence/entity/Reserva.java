package com.meetingroom.manager.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
//import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservas", //uniqueConstraints={@UniqueConstraint(columnNames={ "FK_usuario", "FK_sala", "activa", "fechaReserva" })},
                          indexes = {@Index(name = "fechaR_inx" ,columnList="fechaReserva") })
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
//@OptimisticLocking(type = OptimisticLockType.VERSION)
public class Reserva implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long idreserve;
	
	//Indica si una reserva esta activa
	private EstadoReserva estadoReserva;
	
	//Fecha de la reserva
	private Date fechaReserva;
	
	//Fecha cuando finaliza la reserva.
	private Date fechaHasta;
	
	private String descripcion;
	
	//Lista de invitados para la reserva de sala realizada.
	private List<String> invitados;
	
	@Version
	@Column(name = "regVersion",columnDefinition = "bigint DEFAULT 0", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private long version;
/*
	@OneToMany(mappedBy = "reserved", cascade = CascadeType.ALL)
	@JsonManagedReference(value = "reserve-alerts")
	private Set<Alert> alerts;
*/
	@ManyToOne(optional = false)
	@JoinColumn(name = "FK_usuario")
	@JsonBackReference(value = "usuario-reserva")
	private Usuario usuario;

	@ManyToOne(optional = false)
	@JoinColumn(name = "FK_sala")
	@JsonBackReference(value = "sala-reserva")
	private Sala idSala;
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

