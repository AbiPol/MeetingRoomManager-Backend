package com.meetingroom.manager.presentation.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorFormatCampos {

	private Date fecha = new Date();
    private String mensaje;
    private String url;
    public String code;

    public ErrorFormatCampos( String code,String mensaje, String url) {
        this.mensaje = mensaje;
        this.url = url.replace("uri=","");
        this.code = code;
    }
}
