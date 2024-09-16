package com.codigo.examen_ss.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    private String numDoc;
    private String tipoDoc;
    private String nombres;
    private String apPaterno;
    private String apMaterno;
}
