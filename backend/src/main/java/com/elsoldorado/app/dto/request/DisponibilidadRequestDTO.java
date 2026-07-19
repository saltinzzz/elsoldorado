package com.elsoldorado.app.dto.request;

import jakarta.validation.constraints.NotNull;

public class DisponibilidadRequestDTO {
    @NotNull(message = "Debe indicar el valor de disponibilidad")
    private Boolean disponible;

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
}
