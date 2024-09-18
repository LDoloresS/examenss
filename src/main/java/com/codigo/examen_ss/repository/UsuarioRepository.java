package com.codigo.examen_ss.repository;

import com.codigo.examen_ss.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UsuarioEntity> findByNumDoc(String numDoc);

    List<UsuarioEntity> findByIsEnabled(boolean estado);

    Optional<UsuarioEntity> findByEmail(String email);
/*
    //SQL NATIVO
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM usuario_rol WHERE id_usuario = :id_usuario", nativeQuery = true)
    void usuario_rolD(@Param("id_usuario") Long id_usuario);
 */
}
