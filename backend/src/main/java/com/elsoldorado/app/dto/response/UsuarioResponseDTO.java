package com.elsoldorado.app.dto.response;

import com.elsoldorado.app.model.RolUsuario;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UsuarioResponseDTO {
    private final Long id;
    private final String nombres;
    private final String apellidos;
    private final String nombreCompleto;
    private final String email;
    private final RolUsuario rol;
    private final boolean activo;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime ultimoAcceso;
    private final String telefono;
    private final String direccion;
    private final String cargo;
    private final LocalDate fechaContratacion;

    public UsuarioResponseDTO(Long id, String nombres, String apellidos, String nombreCompleto, String email, RolUsuario rol, boolean activo, LocalDateTime fechaCreacion, LocalDateTime ultimoAcceso, String telefono, String direccion, String cargo, LocalDate fechaContratacion) {
        this.id=id;this.nombres=nombres;this.apellidos=apellidos;this.nombreCompleto=nombreCompleto;this.email=email;this.rol=rol;this.activo=activo;this.fechaCreacion=fechaCreacion;this.ultimoAcceso=ultimoAcceso;this.telefono=telefono;this.direccion=direccion;this.cargo=cargo;this.fechaContratacion=fechaContratacion;
    }
    public Long getId(){return id;} public String getNombres(){return nombres;} public String getApellidos(){return apellidos;} public String getNombreCompleto(){return nombreCompleto;} public String getEmail(){return email;} public RolUsuario getRol(){return rol;} public boolean isActivo(){return activo;} public LocalDateTime getFechaCreacion(){return fechaCreacion;} public LocalDateTime getUltimoAcceso(){return ultimoAcceso;} public String getTelefono(){return telefono;} public String getDireccion(){return direccion;} public String getCargo(){return cargo;} public LocalDate getFechaContratacion(){return fechaContratacion;}
}
