package com.elsoldorado.app.dto.response;

public class LoginResponseDTO {
    private final String token;
    private final Long id;
    private final String username;
    private final String nombreCompleto;
    private final String role;
    private final String telefono;
    private final String direccion;

    public LoginResponseDTO(String token, Long id, String username, String nombreCompleto, String role, String telefono, String direccion) {
        this.token=token;this.id=id;this.username=username;this.nombreCompleto=nombreCompleto;this.role=role;this.telefono=telefono;this.direccion=direccion;
    }
    public String getToken(){return token;} public Long getId(){return id;} public String getUsername(){return username;} public String getNombreCompleto(){return nombreCompleto;} public String getRole(){return role;} public String getTelefono(){return telefono;} public String getDireccion(){return direccion;}
}
