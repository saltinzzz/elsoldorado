package com.elsoldorado.app.dto.response;

public class PlatoResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private boolean disponible;
    private boolean destacado;

    public PlatoResponseDTO(Long id, String nombre, String descripcion,
            double precio, String categoria, boolean disponible, boolean destacado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
        this.destacado = destacado;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
    public boolean isDisponible() { return disponible; }
    public boolean isDestacado() { return destacado; }
}