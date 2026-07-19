package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.PlatoRequestDTO;
import com.elsoldorado.app.model.Categoria;
import com.elsoldorado.app.model.Plato;
import com.elsoldorado.app.repository.CategoriaRepository;
import com.elsoldorado.app.repository.PlatoRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService {
    private final PlatoRepository platoRepository;
    private final CategoriaRepository categoriaRepository;

    public MenuService(PlatoRepository platoRepository, CategoriaRepository categoriaRepository) {
        this.platoRepository = platoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Plato> obtenerTodos() { return platoRepository.findAllByOrderByNombreAsc(); }
    public List<Plato> obtenerMenuCompleto() { return platoRepository.findByDisponibleTrueOrderByNombreAsc(); }
    public List<Plato> obtenerVistaPredefinida() { return platoRepository.findByDisponibleTrueAndVisibleEnInicioTrueOrderByNombreAsc(); }
    public List<Plato> obtenerPlatosDestacados() { return platoRepository.findByDisponibleTrueAndDestacadoTrueOrderByNombreAsc(); }
    public List<Plato> obtenerPorCategoria(String categoria) { return platoRepository.findByDisponibleTrueAndCategoriaNombreIgnoreCaseOrderByNombreAsc(categoria); }
    public List<Plato> buscarPorTexto(String texto) { return platoRepository.buscarPorNombreOCategoria(texto == null ? "" : texto.trim()); }
    public List<Plato> buscarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        if (min == null || max == null || min.signum() < 0 || max.compareTo(min) < 0) throw new IllegalArgumentException("El rango de precios no es válido");
        return platoRepository.buscarPorRangoPrecio(min, max);
    }
    public Optional<Plato> buscarPorId(Long id) { return platoRepository.findById(id); }

    public Plato agregarPlato(PlatoRequestDTO request) {
        Plato plato = new Plato();
        aplicarCambios(plato, request);
        return platoRepository.save(plato);
    }

    public Plato actualizarPlato(Long id, PlatoRequestDTO request) {
        Plato plato = platoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Plato no encontrado"));
        aplicarCambios(plato, request);
        return platoRepository.save(plato);
    }

    public void eliminarPlato(Long id) {
        Plato plato = platoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Plato no encontrado"));
        platoRepository.delete(plato);
    }

    public Plato cambiarDisponibilidad(Long id, boolean disponible) {
        Plato plato = platoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Plato no encontrado"));
        plato.setDisponible(disponible);
        return platoRepository.save(plato);
    }

    private void aplicarCambios(Plato plato, PlatoRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("La categoría seleccionada no existe"));
        plato.setNombre(request.getNombre().trim());
        plato.setDescripcion(request.getDescripcion() == null ? null : request.getDescripcion().trim());
        plato.setPrecio(request.getPrecio());
        plato.setCategoria(categoria);
        plato.setDisponible(request.isDisponible());
        plato.setDestacado(request.isDestacado());
        plato.setVisibleEnInicio(request.isVisibleEnInicio());
    }
}
