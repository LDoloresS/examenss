package com.codigo.examen_ss.repository;

import com.codigo.examen_ss.entity.UsuarioEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
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
