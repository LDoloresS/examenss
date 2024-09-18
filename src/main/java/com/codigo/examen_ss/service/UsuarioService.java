package com.codigo.examen_ss.service;

import com.codigo.examen_ss.aggregates.request.SignInRequest;
import com.codigo.examen_ss.aggregates.request.UsuarioRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.aggregates.response.SignInResponse;
import com.codigo.examen_ss.entity.UsuarioEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UsuarioService {
    ResponseEntity<BaseResponse<UsuarioEntity>> crearUsuario(UsuarioRequest usuarioRequest) throws Exception;

    ResponseEntity<BaseResponse<List<UsuarioEntity>>> listarUsuarios();

    ResponseEntity<BaseResponse<List<UsuarioEntity>>> listarUsuariosAll();

    ResponseEntity<BaseResponse<UsuarioEntity>> buscarUsuarioDni(String dni) throws Exception;

    ResponseEntity<BaseResponse<UsuarioEntity>> actualizarUsuario(Long id, UsuarioRequest usuarioRequest);

    ResponseEntity<BaseResponse<UsuarioEntity>> eliminarUsuario(Long id);

    ResponseEntity<BaseResponse<SignInResponse>> signIn(SignInRequest signInRequest) throws Exception;
}
