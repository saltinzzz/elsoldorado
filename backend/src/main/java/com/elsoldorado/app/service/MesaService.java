package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.MesaRequestDTO;
import com.elsoldorado.app.model.Mesa;
import com.elsoldorado.app.repository.MesaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MesaService {
    private final MesaRepository mesaRepository;
    public MesaService(MesaRepository mesaRepository) { this.mesaRepository = mesaRepository; }

    public List<Mesa> listarMesas() { return mesaRepository.findAllByOrderByNumeroAsc(); }
    public List<Mesa> listarDisponibles() { return mesaRepository.findByDisponibleTrueOrderByNumeroAsc(); }
    public List<Mesa> buscarDisponiblesPorCapacidad(int capacidad) { return mesaRepository.buscarDisponiblesPorCapacidad(capacidad); }
    public Optional<Mesa> buscarPorId(Long id) { return mesaRepository.findById(id); }

    public Mesa crearMesa(MesaRequestDTO request) {
        if (mesaRepository.existsByNumero(request.getNumero())) throw new IllegalArgumentException("Ya existe una mesa con ese número");
        Mesa mesa = new Mesa();
        aplicar(mesa, request);
        return mesaRepository.save(mesa);
    }

    public Mesa actualizarMesa(Long id, MesaRequestDTO request) {
        Mesa mesa = mesaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada"));
        if (mesaRepository.existsByNumeroAndIdNot(request.getNumero(), id)) throw new IllegalArgumentException("Ya existe otra mesa con ese número");
        aplicar(mesa, request);
        return mesaRepository.save(mesa);
    }

    public Mesa cambiarDisponibilidad(Long id, boolean disponible) {
        Mesa mesa = mesaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada"));
        mesa.setDisponible(disponible);
        return mesaRepository.save(mesa);
    }

    public void eliminarMesa(Long id) {
        Mesa mesa = mesaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada"));
        mesaRepository.delete(mesa);
    }

    private void aplicar(Mesa mesa, MesaRequestDTO request) {
        mesa.setNumero(request.getNumero());
        mesa.setCapacidad(request.getCapacidad());
        mesa.setDisponible(request.isDisponible());
    }
}
