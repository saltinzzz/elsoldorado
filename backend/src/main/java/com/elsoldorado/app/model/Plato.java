package com.elsoldorado.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "plato")
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    private boolean disponible;
    private boolean destacado;

    @Column(name = "visible_en_inicio")
    private boolean visibleEnInicio;

    public Plato() {}

    public Plato(Long id, String nombre, String descripcion, BigDecimal precio, Categoria categoria,
                 boolean disponible, boolean destacado, boolean visibleEnInicio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
        this.destacado = destacado;
        this.visibleEnInicio = visibleEnInicio;
    }

    public Long getId() { 
            return id; }
    public void setId(Long id) { 
            this.id = id; }
    public String getNombre() { 
            return nombre; }
    public void setNombre(String nombre) { 
            this.nombre = nombre; }
    public String getDescripcion() { 
            return descripcion; }
    public void setDescripcion(String descripcion) { 
            this.descripcion = descripcion; }
    public BigDecimal getPrecio() { 
            return precio; }
    public void setPrecio(BigDecimal precio) { 
            this.precio = precio; }
    public Categoria getCategoria() { 
            return categoria; }
    public void setCategoria(Categoria categoria) { 
            this.categoria = categoria; }
    public boolean isDisponible() { 
            return disponible; }
    public void setDisponible(boolean disponible) { 
            this.disponible = disponible; }
    public boolean isDestacado() { 
            return destacado; }
    public void setDestacado(boolean destacado) { 
            this.destacado = destacado; }
    public boolean isVisibleEnInicio() { 
            return visibleEnInicio; }
    public void setVisibleEnInicio(boolean visibleEnInicio) { 
            this.visibleEnInicio = visibleEnInicio; }
}
