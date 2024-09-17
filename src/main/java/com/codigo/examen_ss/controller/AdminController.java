package com.codigo.examen_ss.controller;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.entity.UsuarioEntity;
import com.codigo.examen_ss.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examensbss_lin/admin/v1")
@RequiredArgsConstructor
public class AdminController {
    private final UsuarioService usuarioService;

    @GetMapping("/usuarios/listar")
    public ResponseEntity<BaseResponse<List<UsuarioEntity>>> listarUsuariosAll() {
        BaseResponse<List<UsuarioEntity>> response = usuarioService.listarUsuariosAll().getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/usuarios/eliminar/{id}")
    public ResponseEntity<BaseResponse<UsuarioEntity>> eliminarUsuario(@PathVariable("id") Long id) {
        BaseResponse<UsuarioEntity> response = usuarioService.eliminarUsuario(id).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
