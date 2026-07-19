package com.elsoldorado.app.dto.request;

import jakarta.validation.constraints.*;

public class PerfilActualizarRequestDTO {
    @NotBlank @Size(max = 80) private String nombres;
    @NotBlank @Size(max = 80) private String apellidos;
    @Pattern(regexp = "^$|^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y comenzar con 9") private String telefono;
    @Size(max = 200) private String direccion;
    public String getNombres(){return nombres;} public void setNombres(String v){nombres=v;}
    public String getApellidos(){return apellidos;} public void setApellidos(String v){apellidos=v;}
    public String getTelefono(){return telefono;} public void setTelefono(String v){telefono=v;}
    public String getDireccion(){return direccion;} public void setDireccion(String v){direccion=v;}
}
