package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.CategoriaRequestDTO;
import com.elsoldorado.app.model.Categoria;
import com.elsoldorado.app.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    public CategoriaService(CategoriaRepository categoriaRepository) { this.categoriaRepository = categoriaRepository; }

    public List<Categoria> obtenerTodasLasCategorias() { return categoriaRepository.findAll(); }
    public Optional<Categoria> buscarPorId(Long id) { return categoriaRepository.findById(id); }
    public List<Categoria> buscarPorNombre(String texto) { return categoriaRepository.buscarPorNombre(texto == null ? "" : texto.trim()); }

    public Categoria crearCategoria(CategoriaRequestDTO request) {
        String nombre = request.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizarCategoria(Long id, CategoriaRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        String nombre = request.getNombre().trim();
        if (!categoria.getNombre().equalsIgnoreCase(nombre) && categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        categoria.setNombre(nombre);
        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        categoriaRepository.delete(categoria);
    }
}
