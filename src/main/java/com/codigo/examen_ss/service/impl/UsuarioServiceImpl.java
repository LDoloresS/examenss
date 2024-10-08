package com.codigo.examen_ss.service.impl;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.request.SignInRequest;
import com.codigo.examen_ss.aggregates.request.UsuarioRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.aggregates.response.ReniecResponse;
import com.codigo.examen_ss.aggregates.response.SignInResponse;
import com.codigo.examen_ss.client.ReniecClient;
import com.codigo.examen_ss.entity.Rol;
import com.codigo.examen_ss.entity.Role;
import com.codigo.examen_ss.entity.UsuarioEntity;
import com.codigo.examen_ss.repository.RolRepository;
import com.codigo.examen_ss.repository.UsuarioRepository;
import com.codigo.examen_ss.service.JwtService;
import com.codigo.examen_ss.service.RedisService;
import com.codigo.examen_ss.service.UsuarioService;
import com.codigo.examen_ss.util.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ReniecClient reniecClient;
    private final RedisService redisService;
    private final UserDetailsServiceImpl userDetailsService;

    private final RolRepository rolRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, ReniecClient reniecClient,
                              RedisService redisService, UserDetailsServiceImpl userDetailsService,
                              RolRepository rolRepository, AuthenticationManager authenticationManager,
                              JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.reniecClient = reniecClient;
        this.redisService = redisService;
        this.userDetailsService = userDetailsService;
        this.rolRepository = rolRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Value("${token.api}")
    private String tokenapi;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<UsuarioEntity>> crearUsuario(UsuarioRequest usuarioRequest) throws Exception {
        BaseResponse<UsuarioEntity> baseResponse = new BaseResponse<>();
        try {
            boolean existEmail = usuarioRepository.existsByEmail(usuarioRequest.getEmail());
            Rol rol = getRoles(Role.valueOf(usuarioRequest.getRol()));
            if (existEmail || Objects.isNull(rol) || rol.equals("")) {
                return null;
            }
            UsuarioEntity usuarioEntity = getUsuarioEntity(usuarioRequest);
            if (Objects.nonNull(usuarioEntity)) {
                usuarioEntity.setRoles(Collections.singleton(rol));
                usuarioEntity.setPassword(new BCryptPasswordEncoder().encode(usuarioRequest.getPassword()));
                usuarioEntity.setIsEnabled(Constants.STATUS_ACTIVE);

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
        BaseResponse<List<UsuarioEntity>> baseResponse = new BaseResponse<>();
        List<UsuarioEntity> usuarioEntityList = usuarioRepository.findByIsEnabled(Constants.STATUS_ACTIVE);
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
    public ResponseEntity<BaseResponse<List<UsuarioEntity>>> listarUsuariosAll() {
        BaseResponse<List<UsuarioEntity>> baseResponse = new BaseResponse<>();
        List<UsuarioEntity> usuarioEntityList = usuarioRepository.findAll();
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
    public ResponseEntity<BaseResponse<UsuarioEntity>> buscarUsuarioDni(String dni) throws Exception {
        BaseResponse<UsuarioEntity> baseResponse = new BaseResponse<>();
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
    @Transactional
    public ResponseEntity<BaseResponse<UsuarioEntity>> actualizarUsuario(Long id, UsuarioRequest usuarioRequest) {
        BaseResponse<UsuarioEntity> baseResponse = new BaseResponse<>();
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        Rol rol = getRoles(Role.valueOf(usuarioRequest.getRol()));
        if (usuarioExistente.isEmpty() || Objects.isNull(rol) || rol.equals("")) {
            return null;
        }
        UsuarioEntity usuarioActualizar = getUsuarioEntityUpdate(usuarioRequest, usuarioExistente.get());
        if (Objects.nonNull(usuarioActualizar)) {
            usuarioActualizar.setRoles(Collections.singleton(rol));
            usuarioActualizar.setPassword(new BCryptPasswordEncoder().encode(usuarioRequest.getPassword()));
            usuarioActualizar.setIsEnabled(Constants.STATUS_ACTIVE);
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
        BaseResponse<UsuarioEntity> baseResponse = new BaseResponse<>();
        if (usuarioRepository.existsById(id)) {
            UsuarioEntity usuarioRecuperado = usuarioRepository.findById(id).orElse(null);
            usuarioRecuperado.setIsEnabled(Constants.STATUS_INACTIVE);
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

    @Override
    public ResponseEntity<BaseResponse<SignInResponse>> signIn(SignInRequest signInRequest) throws Exception {
        BaseResponse<SignInResponse> baseResponse = new BaseResponse<>();
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getEmail(), signInRequest.getPassword()));
        if (auth.isAuthenticated()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(signInRequest.getEmail());
            var token = jwtService.generateToken(userDetails);
            SignInResponse response = new SignInResponse();
            response.setToken(token);
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);
            baseResponse.setObjeto(Optional.of(response));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_LOGIN);
            baseResponse.setMessage(Constants.ERROR_MESS_LOGIN);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    private UsuarioEntity getUsuarioEntity(UsuarioRequest usuarioRequest) {
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        ReniecResponse response = executionReniec(usuarioRequest.getNumDoc());
        if (Objects.nonNull(response)) {
            usuarioEntity.setNombres(response.getNombres());
            usuarioEntity.setApPaterno(response.getApellidoPaterno());
            usuarioEntity.setApMaterno(response.getApellidoMaterno());
            usuarioEntity.setTipoDoc(response.getTipoDocumento());
            usuarioEntity.setNumDoc(response.getNumeroDocumento());

            usuarioEntity.setEmail(usuarioRequest.getEmail());
            usuarioEntity.setIsAccountNonExpired(Constants.STATUS_ACTIVE);
            usuarioEntity.setIsAccountNonLocked(Constants.STATUS_ACTIVE);
            usuarioEntity.setIsCredentialsNonExpired(Constants.STATUS_ACTIVE);

            usuarioEntity.setUsua_crea(Constants.USU_CREA);
            usuarioEntity.setDate_crea(new Timestamp(System.currentTimeMillis()));
        }
        return usuarioEntity;
    }

    private ReniecResponse executionReniec(String dni) {
        String auth = "Bearer " + tokenapi;
        return reniecClient.getPersonaReniec(dni, auth);
    }

    private UsuarioEntity getUsuarioEntityUpdate(UsuarioRequest usuarioRequest, UsuarioEntity usuarioDB) {
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        if (usuarioRequest != null) {
            redisService.deleteByKey(Constants.REDIS_KEY_API_PERSON + usuarioRequest.getNumDoc());

            usuarioEntity.setId(usuarioDB.getId());
            usuarioEntity.setNombres(usuarioRequest.getNombres());
            usuarioEntity.setApPaterno(usuarioRequest.getApPaterno());
            usuarioEntity.setApMaterno(usuarioRequest.getApMaterno());
            usuarioEntity.setTipoDoc(usuarioRequest.getTipoDoc());
            usuarioEntity.setNumDoc(usuarioRequest.getNumDoc());

            usuarioEntity.setEmail(usuarioRequest.getEmail());
            usuarioEntity.setIsAccountNonExpired(Constants.STATUS_ACTIVE);
            usuarioEntity.setIsAccountNonLocked(Constants.STATUS_ACTIVE);
            usuarioEntity.setIsCredentialsNonExpired(Constants.STATUS_ACTIVE);

            usuarioEntity.setUsua_upda(Constants.USU_CREA);
            usuarioEntity.setDate_upda(new Timestamp(System.currentTimeMillis()));
            usuarioEntity.setUsua_crea(usuarioDB.getUsua_crea());
            usuarioEntity.setDate_crea(usuarioDB.getDate_crea());
            usuarioEntity.setUsua_dele(usuarioDB.getUsua_dele());
            usuarioEntity.setDate_dele(usuarioDB.getDate_dele());
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

    private Rol getRoles(Role rolBuscado) {
        return rolRepository.findByNombre(rolBuscado.name())
                .orElseThrow(() -> new RuntimeException("EL ROL BUSCADO NO EXISTE: "
                        + rolBuscado.name()));
    }
}
