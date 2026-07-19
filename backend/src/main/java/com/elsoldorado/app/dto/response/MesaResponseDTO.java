package com.elsoldorado.app.dto.response;

public class MesaResponseDTO {
    private Long id;
    private int numero;
    private int capacidad;
    private boolean disponible;

    public MesaResponseDTO(Long id, int numero, int capacidad, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public int getNumero() { return numero; }
    public int getCapacidad() { return capacidad; }
    public boolean isDisponible() { return disponible; }
}