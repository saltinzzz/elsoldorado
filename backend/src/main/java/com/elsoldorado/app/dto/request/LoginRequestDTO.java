package com.elsoldorado.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo válido")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
