package com.elsoldorado.app.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class MesaRequestDTO {
    @Min(value = 1, message = "El número de mesa debe ser mayor que cero")
    private int numero;

    @Min(value = 1, message = "La capacidad debe ser mayor que cero")
    @Max(value = 20, message = "La capacidad máxima registrada es 20")
    private int capacidad;

    private boolean disponible = true;

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
