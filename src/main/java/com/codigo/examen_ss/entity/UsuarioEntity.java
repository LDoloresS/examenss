package com.codigo.examen_ss.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "usuarios")
@Data
public class UsuarioEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombres;
    @Column(name = "apPaterno", nullable = false, length = 255)
    private String apPaterno;
    @Column(name = "apMaterno", nullable = false, length = 255)
    private String apMaterno;
    @Column(name = "tipoDoc", nullable = false, length = 2)
    private String tipoDoc;
    @Column(name = "numDoc", nullable = false, length = 8)
    private String numDoc;
    @Column(name = "estado", nullable = false)
    private Integer estado;

    @Column(name = "email", nullable = false, length = 255)
    private String email;
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    @Column(name = "isAccountNonExpired", nullable = false)
    private Boolean isAccountNonExpired;
    @Column(name = "isAccountNonLocked", nullable = false)
    private Boolean isAccountNonLocked;
    @Column(name = "isCredentialsNonExpired", nullable = false)
    private Boolean isCredentialsNonExpired;
    @Column(name = "isEnabled", nullable = false)
    private Boolean isEnabled;

    @Column(name = "usua_crea", nullable = false, length = 255)
    private String usua_crea;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_crea", nullable = false)
    private Timestamp date_crea;
    @Column(name = "usua_upda", length = 255)
    private String usua_upda;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_upda")
    private Timestamp date_upda;
    @Column(name = "usua_dele", length = 255)
    private String usua_dele;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_dele")
    private Timestamp date_dele;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "id_rol", referencedColumnName = "id", nullable = false))
    private Set<Rol> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
