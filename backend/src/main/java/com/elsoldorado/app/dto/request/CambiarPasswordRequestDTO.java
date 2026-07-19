package com.elsoldorado.app.dto.request;

import jakarta.validation.constraints.*;

public class CambiarPasswordRequestDTO {
    @NotBlank private String passwordActual;
    @NotBlank @Size(min=8,max=72) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La nueva contraseña debe incluir letras y números") private String passwordNueva;
    public String getPasswordActual(){return passwordActual;} public void setPasswordActual(String v){passwordActual=v;}
    public String getPasswordNueva(){return passwordNueva;} public void setPasswordNueva(String v){passwordNueva=v;}
}
