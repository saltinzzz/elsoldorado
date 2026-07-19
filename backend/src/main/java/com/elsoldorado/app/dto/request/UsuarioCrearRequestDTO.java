package com.elsoldorado.app.dto.request;

import com.elsoldorado.app.model.RolUsuario;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class UsuarioCrearRequestDTO {
    @NotBlank @Size(max = 80) private String nombres;
    @NotBlank @Size(max = 80) private String apellidos;
    @NotBlank @Email @Size(max = 150) private String email;
    @NotBlank @Size(min = 8, max = 72) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe incluir letras y números") private String password;
    @NotNull private RolUsuario rol;
    @Pattern(regexp = "^$|^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y comenzar con 9") private String telefono;
    @Size(max = 200) private String direccion;
    @Size(max = 80) private String cargo;
    private LocalDate fechaContratacion;

    public String getNombres(){return nombres;} public void setNombres(String v){nombres=v;}
    public String getApellidos(){return apellidos;} public void setApellidos(String v){apellidos=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public RolUsuario getRol(){return rol;} public void setRol(RolUsuario v){rol=v;}
    public String getTelefono(){return telefono;} public void setTelefono(String v){telefono=v;}
    public String getDireccion(){return direccion;} public void setDireccion(String v){direccion=v;}
    public String getCargo(){return cargo;} public void setCargo(String v){cargo=v;}
    public LocalDate getFechaContratacion(){return fechaContratacion;} public void setFechaContratacion(LocalDate v){fechaContratacion=v;}
}
