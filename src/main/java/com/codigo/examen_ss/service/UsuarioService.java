package com.codigo.examen_ss.service;

import com.codigo.examen_ss.aggregates.request.UsuarioRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.entity.UsuarioEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UsuarioService {
    ResponseEntity<BaseResponse<UsuarioEntity>> crearUsuario(UsuarioRequest usuarioRequest);

    ResponseEntity<BaseResponse<List<UsuarioEntity>>> listarUsuarios();

    ResponseEntity<BaseResponse<UsuarioEntity>> buscarUsuarioDni(String dni);

    ResponseEntity<BaseResponse<UsuarioEntity>> actualizarUsuario(Long id, UsuarioRequest usuarioRequest);

    ResponseEntity<BaseResponse<UsuarioEntity>> eliminarUsuario(Long id);
}
