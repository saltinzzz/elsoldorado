package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.RegistroClienteRequestDTO;
import com.elsoldorado.app.model.Cliente;
import com.elsoldorado.app.model.RolUsuario;
import com.elsoldorado.app.model.Usuario;
import com.elsoldorado.app.repository.ClienteRepository;
import com.elsoldorado.app.repository.EmpleadoRepository;
import com.elsoldorado.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository usuarioRepository;
    @Mock ClienteRepository clienteRepository;
    @Mock EmpleadoRepository empleadoRepository;
    @Mock PasswordEncoder passwordEncoder;
    UsuarioService service;

    @BeforeEach void setUp() { service = new UsuarioService(usuarioRepository, clienteRepository, empleadoRepository, passwordEncoder); }

    @Test void registraClienteConPasswordCifrado() {
        RegistroClienteRequestDTO r = new RegistroClienteRequestDTO();
        r.setNombres("Ana"); r.setApellidos("Torres"); r.setEmail("ANA@MAIL.COM"); r.setTelefono("987654321"); r.setPassword("clave123");
        when(usuarioRepository.existsByEmailIgnoreCase("ana@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("clave123")).thenReturn("HASH");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> { Usuario u=i.getArgument(0);u.setId(10L);return u; });
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));
        when(clienteRepository.findByUsuarioId(10L)).thenReturn(Optional.of(new Cliente()));
        var dto=service.registrarCliente(r);
        assertEquals("ana@mail.com",dto.getEmail());
        assertEquals(RolUsuario.CLIENTE,dto.getRol());
        verify(passwordEncoder).encode("clave123");
    }

    @Test void rechazaCorreoDuplicado() {
        RegistroClienteRequestDTO r = new RegistroClienteRequestDTO();
        r.setNombres("Ana"); r.setApellidos("Torres"); r.setEmail("ana@mail.com"); r.setTelefono("987654321"); r.setPassword("clave123");
        when(usuarioRepository.existsByEmailIgnoreCase("ana@mail.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.registrarCliente(r));
        verify(usuarioRepository, never()).save(any());
    }
}
