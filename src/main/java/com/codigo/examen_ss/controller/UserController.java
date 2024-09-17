package com.codigo.examen_ss.controller;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.request.UsuarioRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.entity.UsuarioEntity;
import com.codigo.examen_ss.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examensbss_lin/user/v1")
@RequiredArgsConstructor
public class UserController {
    private final UsuarioService usuarioService;

    @GetMapping("usuarios/listar")
    public ResponseEntity<BaseResponse<List<UsuarioEntity>>> listarUsuarios() {
        BaseResponse<List<UsuarioEntity>> response = usuarioService.listarUsuarios().getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("usuarios/{dni}")
    public ResponseEntity<BaseResponse<UsuarioEntity>> buscarUsuarioDni(@PathVariable("dni") String dni) {
        BaseResponse<UsuarioEntity> response = usuarioService.buscarUsuarioDni(dni).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("usuarios/actualizar/{id}")
    public ResponseEntity<BaseResponse<UsuarioEntity>> actualizarUsuario(@PathVariable("id") Long id, @RequestBody UsuarioRequest usuarioRequest) {
        BaseResponse<UsuarioEntity> response = usuarioService.actualizarUsuario(id, usuarioRequest).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
