package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.*;
import com.elsoldorado.app.dto.response.UsuarioResponseDTO;
import com.elsoldorado.app.model.*;
import com.elsoldorado.app.repository.ClienteRepository;
import com.elsoldorado.app.repository.EmpleadoRepository;
import com.elsoldorado.app.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository,
                          EmpleadoRepository empleadoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO registrarCliente(RegistroClienteRequestDTO request) {
        validarEmailDisponible(request.getEmail(), null);
        Usuario usuario = nuevoUsuario(request.getNombres(), request.getApellidos(), request.getEmail(),
                request.getPassword(), RolUsuario.CLIENTE);
        usuario = usuarioRepository.save(usuario);
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setTelefono(limpiar(request.getTelefono()));
        cliente.setDireccion(limpiar(request.getDireccion()));
        clienteRepository.save(cliente);
        return toDto(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAllByOrderByFechaCreacionDesc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarClientes() {
        return usuarioRepository.findByRolOrderByNombresAsc(RolUsuario.CLIENTE).stream()
                .filter(Usuario::isActivo).map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarDto(Long id) { return toDto(buscarEntidad(id)); }

    @Transactional(readOnly = true)
    public Usuario buscarEntidad(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("No se pudo identificar al usuario autenticado");
        }
        return buscarPorEmail(authentication.getName());
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioCrearRequestDTO request) {
        validarEmailDisponible(request.getEmail(), null);
        validarPerfil(request.getRol(), request.getTelefono(), request.getCargo());
        Usuario usuario = nuevoUsuario(request.getNombres(), request.getApellidos(), request.getEmail(),
                request.getPassword(), request.getRol());
        usuario = usuarioRepository.save(usuario);
        crearPerfil(usuario, request.getTelefono(), request.getDireccion(), request.getCargo(), request.getFechaContratacion());
        return toDto(usuario);
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioActualizarRequestDTO request, String emailOperador) {
        Usuario usuario = buscarEntidad(id);
        validarEmailDisponible(request.getEmail(), id);
        validarPerfil(request.getRol(), request.getTelefono(), request.getCargo());
        if (usuario.getEmail().equalsIgnoreCase(emailOperador) && usuario.getRol() == RolUsuario.ADMIN
                && request.getRol() != RolUsuario.ADMIN) {
            throw new IllegalArgumentException("No puedes quitarte tu propio rol de administrador");
        }
        RolUsuario rolAnterior = usuario.getRol();
        usuario.setNombres(limpiarRequerido(request.getNombres(), "Los nombres son obligatorios"));
        usuario.setApellidos(limpiarRequerido(request.getApellidos(), "Los apellidos son obligatorios"));
        usuario.setEmail(normalizarEmail(request.getEmail()));
        usuario.setRol(request.getRol());
        usuarioRepository.save(usuario);

        if (rolAnterior != request.getRol()) {
            clienteRepository.deleteByUsuarioId(id);
            empleadoRepository.deleteByUsuarioId(id);
        }
        actualizarOCrearPerfil(usuario, request.getTelefono(), request.getDireccion(), request.getCargo(), request.getFechaContratacion());
        return toDto(usuario);
    }

    @Transactional
    public UsuarioResponseDTO cambiarEstado(Long id, boolean activo, String emailOperador) {
        Usuario usuario = buscarEntidad(id);
        if (usuario.getEmail().equalsIgnoreCase(emailOperador) && !activo) {
            throw new IllegalArgumentException("No puedes desactivar tu propia cuenta");
        }
        if (usuario.getRol() == RolUsuario.ADMIN && usuario.isActivo() && !activo
                && usuarioRepository.countByRolAndActivoTrue(RolUsuario.ADMIN) <= 1) {
            throw new IllegalArgumentException("Debe permanecer al menos un administrador activo");
        }
        usuario.setActivo(activo);
        return toDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public void restablecerPassword(Long id, String nuevaPassword) {
        Usuario usuario = buscarEntidad(id);
        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioResponseDTO actualizarPerfil(Authentication authentication, PerfilActualizarRequestDTO request) {
        Usuario usuario = obtenerAutenticado(authentication);
        usuario.setNombres(limpiarRequerido(request.getNombres(), "Los nombres son obligatorios"));
        usuario.setApellidos(limpiarRequerido(request.getApellidos(), "Los apellidos son obligatorios"));
        usuarioRepository.save(usuario);
        if (usuario.getRol() == RolUsuario.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId()).orElseGet(() -> {
                Cliente nuevo = new Cliente(); nuevo.setUsuario(usuario); return nuevo;
            });
            cliente.setTelefono(limpiar(request.getTelefono()));
            cliente.setDireccion(limpiar(request.getDireccion()));
            clienteRepository.save(cliente);
        }
        return toDto(usuario);
    }

    @Transactional
    public void cambiarPassword(Authentication authentication, CambiarPasswordRequestDTO request) {
        Usuario usuario = obtenerAutenticado(authentication);
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }
        if (passwordEncoder.matches(request.getPasswordNueva(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente de la actual");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void registrarUltimoAcceso(Usuario usuario) {
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO perfil(Authentication authentication) { return toDto(obtenerAutenticado(authentication)); }

    private Usuario nuevoUsuario(String nombres, String apellidos, String email, String password, RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setNombres(limpiarRequerido(nombres, "Los nombres son obligatorios"));
        usuario.setApellidos(limpiarRequerido(apellidos, "Los apellidos son obligatorios"));
        usuario.setEmail(normalizarEmail(email));
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        return usuario;
    }

    private void crearPerfil(Usuario usuario, String telefono, String direccion, String cargo, LocalDate fechaContratacion) {
        if (usuario.getRol() == RolUsuario.CLIENTE) {
            Cliente cliente = new Cliente();
            cliente.setUsuario(usuario); cliente.setTelefono(limpiar(telefono)); cliente.setDireccion(limpiar(direccion));
            clienteRepository.save(cliente);
        } else if (usuario.getRol() == RolUsuario.EMPLEADO) {
            Empleado empleado = new Empleado();
            empleado.setUsuario(usuario); empleado.setCargo(limpiarRequerido(cargo, "El cargo es obligatorio para un empleado"));
            empleado.setFechaContratacion(fechaContratacion == null ? LocalDate.now() : fechaContratacion);
            empleadoRepository.save(empleado);
        }
    }

    private void actualizarOCrearPerfil(Usuario usuario, String telefono, String direccion, String cargo, LocalDate fechaContratacion) {
        if (usuario.getRol() == RolUsuario.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId()).orElseGet(() -> {
                Cliente nuevo = new Cliente(); nuevo.setUsuario(usuario); return nuevo;
            });
            cliente.setTelefono(limpiar(telefono)); cliente.setDireccion(limpiar(direccion)); clienteRepository.save(cliente);
        } else if (usuario.getRol() == RolUsuario.EMPLEADO) {
            Empleado empleado = empleadoRepository.findByUsuarioId(usuario.getId()).orElseGet(() -> {
                Empleado nuevo = new Empleado(); nuevo.setUsuario(usuario); return nuevo;
            });
            empleado.setCargo(limpiarRequerido(cargo, "El cargo es obligatorio para un empleado"));
            empleado.setFechaContratacion(fechaContratacion == null ? LocalDate.now() : fechaContratacion);
            empleadoRepository.save(empleado);
        }
    }

    private void validarEmailDisponible(String email, Long idActual) {
        String normalizado = normalizarEmail(email);
        boolean existe = idActual == null ? usuarioRepository.existsByEmailIgnoreCase(normalizado)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(normalizado, idActual);
        if (existe) throw new IllegalArgumentException("Ya existe una cuenta registrada con ese correo");
    }

    private void validarPerfil(RolUsuario rol, String telefono, String cargo) {
        if (rol == null) throw new IllegalArgumentException("El rol es obligatorio");
        if (rol == RolUsuario.CLIENTE && (telefono == null || telefono.isBlank())) {
            throw new IllegalArgumentException("El teléfono es obligatorio para un cliente");
        }
        if (rol == RolUsuario.EMPLEADO && (cargo == null || cargo.isBlank())) {
            throw new IllegalArgumentException("El cargo es obligatorio para un empleado");
        }
    }

    private UsuarioResponseDTO toDto(Usuario usuario) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId()).orElse(null);
        Empleado empleado = empleadoRepository.findByUsuarioId(usuario.getId()).orElse(null);
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNombres(), usuario.getApellidos(), usuario.getNombreCompleto(),
                usuario.getEmail(), usuario.getRol(), usuario.isActivo(), usuario.getFechaCreacion(), usuario.getUltimoAcceso(),
                cliente == null ? null : cliente.getTelefono(), cliente == null ? null : cliente.getDireccion(),
                empleado == null ? null : empleado.getCargo(), empleado == null ? null : empleado.getFechaContratacion());
    }

    private String normalizarEmail(String value) { return limpiarRequerido(value, "El correo es obligatorio").toLowerCase(); }
    private String limpiar(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String limpiarRequerido(String value, String mensaje) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(mensaje);
        return value.trim();
    }
}
