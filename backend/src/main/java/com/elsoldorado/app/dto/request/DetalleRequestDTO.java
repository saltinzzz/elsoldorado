package com.elsoldorado.app.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DetalleRequestDTO {
    @NotNull(message = "El plato es obligatorio")
    private Long idPlato;

    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 20, message = "La cantidad máxima por plato es 20")
    private int cantidad;

    public Long getIdPlato() { return idPlato; }
    public void setIdPlato(Long idPlato) { this.idPlato = idPlato; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
