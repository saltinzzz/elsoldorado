package com.elsoldorado.app.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class PlatoRequestDTO {
    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener como máximo dos decimales")
    private BigDecimal precio;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private boolean disponible = true;
    private boolean destacado;
    private boolean visibleEnInicio;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public boolean isDestacado() { return destacado; }
    public void setDestacado(boolean destacado) { this.destacado = destacado; }
    public boolean isVisibleEnInicio() { return visibleEnInicio; }
    public void setVisibleEnInicio(boolean visibleEnInicio) { this.visibleEnInicio = visibleEnInicio; }
}
