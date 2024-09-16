package com.codigo.examen_ss.service.impl;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.request.UsuarioRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.aggregates.response.ReniecResponse;
import com.codigo.examen_ss.client.ReniecClient;
import com.codigo.examen_ss.entity.UsuarioEntity;
import com.codigo.examen_ss.repository.UsuarioRepository;
import com.codigo.examen_ss.service.RedisService;
import com.codigo.examen_ss.service.UsuarioService;
import com.codigo.examen_ss.util.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ReniecClient reniecClient;
    private final RedisService redisService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, ReniecClient reniecClient, RedisService redisService) {
        this.usuarioRepository = usuarioRepository;
        this.reniecClient = reniecClient;
        this.redisService = redisService;
    }

    @Value("${token.api}")
    private String tokenapi;

    @Override
    public ResponseEntity<BaseResponse<UsuarioEntity>> crearUsuario(UsuarioRequest usuarioRequest) {
        BaseResponse<UsuarioEntity> baseResponse = new BaseResponse<UsuarioEntity>();
        try {
            UsuarioEntity usuarioEntity = getEntity(usuarioRequest);
            if (Objects.nonNull(usuarioEntity)) {
                usuarioEntity.setEstado(Constants.STATUS_ACTIVE);
                baseResponse.setCode(Constants.OK_DNI_CODE);
                baseResponse.setMessage(Constants.OK_DNI_MESS);
                baseResponse.setObjeto(Optional.of(usuarioRepository.save(usuarioEntity)));
            }
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setCode(Constants.ERROR_DNI_CODE);
            baseResponse.setMessage(Constants.ERROR_DNI_MESS + " -> " + e.getMessage());
            baseResponse.setObjeto(Optional.empty());
            return ResponseEntity.ok(baseResponse);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<List<UsuarioEntity>>> listarUsuarios() {
        BaseResponse<List<UsuarioEntity>> baseResponse = new BaseResponse<List<UsuarioEntity>>();
        List<UsuarioEntity> usuarioEntityList = usuarioRepository.findByEstado(Constants.STATUS_ACTIVE);
        if (Objects.nonNull(usuarioEntityList)) {
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);
            baseResponse.setObjeto(Optional.of(usuarioEntityList));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_LIST_EMPTY);
            baseResponse.setMessage(Constants.ERROR_MESS_LIST_EMPTY);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<UsuarioEntity>> buscarUsuarioDni(String dni) {
        BaseResponse<UsuarioEntity> baseResponse = new BaseResponse<UsuarioEntity>();
        try {
            Optional<UsuarioEntity> usuarioBuscar = executionBuscarUsuarioDni(dni);
            if (usuarioBuscar.isPresent()) {
                baseResponse.setCode(Constants.OK_DNI_CODE);
                baseResponse.setMessage(Constants.OK_DNI_MESS);
                baseResponse.setObjeto(usuarioBuscar);
            } else {
                baseResponse.setCode(Constants.ERROR_DNI_CODE);
                baseResponse.setMessage(Constants.ERROR_DNI_MESS);
                baseResponse.setObjeto(Optional.empty());
            }
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setCode(Constants.ERROR_DNI_CODE);
            baseResponse.setMessage(Constants.ERROR_DNI_MESS + " -> " + e.getMessage());
            baseResponse.setObjeto(Optional.empty());
            return ResponseEntity.ok(baseResponse);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<UsuarioEntity>> actualizarUsuario(Long id, UsuarioRequest usuarioRequest) {
        BaseResponse<UsuarioEntity> baseResponse = new BaseResponse<UsuarioEntity>();
        Optional<UsuarioEntity> datosActualizar = usuarioRepository.findById(id);
        if (datosActualizar.isPresent()) {
            UsuarioEntity usuarioActualizar = getEntityUpdate(usuarioRequest, datosActualizar.get());
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);
            baseResponse.setObjeto(Optional.of(usuarioRepository.save(usuarioActualizar)));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_UPD);
            baseResponse.setMessage(Constants.ERROR_MESS_UPD);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<UsuarioEntity>> eliminarUsuario(Long id) {
        BaseResponse<UsuarioEntity> baseResponse = new BaseResponse<UsuarioEntity>();
        if (usuarioRepository.existsById(id)) {
            UsuarioEntity usuarioRecuperado = usuarioRepository.findById(id).orElse(null);
            usuarioRecuperado.setEstado(0);
            usuarioRecuperado.setUsua_dele(Constants.USU_CREA);
            usuarioRecuperado.setDate_dele(new Timestamp(System.currentTimeMillis()));
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);
            baseResponse.setObjeto(Optional.of(usuarioRepository.save(usuarioRecuperado)));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_DEL);
            baseResponse.setMessage(Constants.ERROR_MESS_DEL);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    private UsuarioEntity getEntity(UsuarioRequest usuarioRequest) {
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        ReniecResponse response = executionReniec(usuarioRequest.getNumDoc());
        if (Objects.nonNull(response)) {
            usuarioEntity.setNombres(response.getNombres());
            usuarioEntity.setApPaterno(response.getApellidoPaterno());
            usuarioEntity.setApMaterno(response.getApellidoMaterno());
            usuarioEntity.setTipoDoc(response.getTipoDocumento());
            usuarioEntity.setNumDoc(response.getNumeroDocumento());
            usuarioEntity.setUsua_crea(Constants.USU_CREA);
            usuarioEntity.setDate_crea(new Timestamp(System.currentTimeMillis()));
        }
        return usuarioEntity;
    }

    private ReniecResponse executionReniec(String dni) {
        String auth = "Bearer " + tokenapi;
        return reniecClient.getPersonaReniec(dni, auth);
    }

    private UsuarioEntity getEntityUpdate(UsuarioRequest usuarioRequest, UsuarioEntity usuarioEntity) {
        if (usuarioRequest != null) {
            redisService.deleteByKey(Constants.REDIS_KEY_API_PERSON + usuarioRequest.getNumDoc());
            usuarioEntity.setNombres(usuarioRequest.getNombres());
            usuarioEntity.setApPaterno(usuarioRequest.getApPaterno());
            usuarioEntity.setApMaterno(usuarioRequest.getApMaterno());
            usuarioEntity.setTipoDoc(usuarioRequest.getTipoDoc());
            usuarioEntity.setNumDoc(usuarioRequest.getNumDoc());
            usuarioEntity.setUsua_upda(Constants.USU_CREA);
            usuarioEntity.setDate_upda(new Timestamp(System.currentTimeMillis()));
        }
        return usuarioEntity;
    }

    private Optional<UsuarioEntity> executionBuscarUsuarioDni(String dni) {
        String redisInfo = redisService.getValueByKey(Constants.REDIS_KEY_API_PERSON + dni);
        if (Objects.nonNull(redisInfo)) {
            return Optional.of(Utils.convertirDesdeString(redisInfo, UsuarioEntity.class));
        } else {
            Optional<UsuarioEntity> usuarioBuscar = usuarioRepository.findByNumDoc(dni);
            if (usuarioBuscar.isPresent()) {
                UsuarioEntity usuarioEntity = usuarioBuscar.get();
                String dataForRedis = Utils.convertirAString(usuarioEntity);
                redisService.saveKeyValue(Constants.REDIS_KEY_API_PERSON + dni, dataForRedis, Constants.REDIS_EXP);
            }
            return usuarioBuscar;
        }
    }
}
