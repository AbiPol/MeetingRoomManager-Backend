package com.meetingroom.manager.persistence.entity;

import lombok.Getter;

@Getter
public enum EstadoSala {
   NO_DISPONIBLE(0), DISPONIBLE(1);

   int idEstado;

   private EstadoSala(int idEstado) {
       this.idEstado = idEstado;
   }
/*
   public int getIdEstado() {
       return idEstado;
   }
   */
}
