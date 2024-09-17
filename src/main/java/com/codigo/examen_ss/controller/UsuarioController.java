package com.codigo.examen_ss.controller;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.request.UsuarioRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.entity.UsuarioEntity;
import com.codigo.examen_ss.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examensbss_lin/usuarios/v1")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/crear")
    public ResponseEntity<BaseResponse<UsuarioEntity>> crearUsuario(@RequestBody UsuarioRequest usuarioRequest) throws Exception {
        BaseResponse<UsuarioEntity> response = usuarioService.crearUsuario(usuarioRequest).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<BaseResponse<List<UsuarioEntity>>> listarUsuarios() {
        BaseResponse<List<UsuarioEntity>> response = usuarioService.listarUsuarios().getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{dni}")
    public ResponseEntity<BaseResponse<UsuarioEntity>> buscarUsuarioDni(@PathVariable("dni") String dni) throws Exception {
        BaseResponse<UsuarioEntity> response = usuarioService.buscarUsuarioDni(dni).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<BaseResponse<UsuarioEntity>> actualizarUsuario(@PathVariable("id") Long id, @RequestBody UsuarioRequest usuarioRequest) {
        BaseResponse<UsuarioEntity> response = usuarioService.actualizarUsuario(id, usuarioRequest).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<BaseResponse<UsuarioEntity>> eliminarUsuario(@PathVariable("id") Long id) {
        BaseResponse<UsuarioEntity> response = usuarioService.eliminarUsuario(id).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
