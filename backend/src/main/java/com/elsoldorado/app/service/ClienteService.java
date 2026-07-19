package com.elsoldorado.app.service;

import com.elsoldorado.app.model.Cliente;
import com.elsoldorado.app.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    public ClienteService(ClienteRepository clienteRepository) { this.clienteRepository = clienteRepository; }

    public List<Cliente> listarClientes() { return clienteRepository.findAll(); }
    public Optional<Cliente> buscarPorId(Long id) { return clienteRepository.findById(id); }
    public Optional<Cliente> buscarPorUsuario(Long usuarioId) { return clienteRepository.findByUsuarioId(usuarioId); }

    @Transactional
    public Cliente guardarPerfil(Cliente cliente) {
        if (cliente.getUsuario() == null || cliente.getUsuario().getId() == null) {
            throw new IllegalArgumentException("El perfil debe estar asociado a un usuario");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        cliente.setTelefono(cliente.getTelefono().trim());
        cliente.setDireccion(cliente.getDireccion() == null || cliente.getDireccion().isBlank() ? null : cliente.getDireccion().trim());
        return clienteRepository.save(cliente);
    }
}
