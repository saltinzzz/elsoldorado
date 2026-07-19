package com.elsoldorado.app.dto.request;

import jakarta.validation.constraints.*;

public class RestablecerPasswordRequestDTO {
    @NotBlank @Size(min=8,max=72) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe incluir letras y números") private String passwordNueva;
    public String getPasswordNueva(){return passwordNueva;} public void setPasswordNueva(String v){passwordNueva=v;}
}
