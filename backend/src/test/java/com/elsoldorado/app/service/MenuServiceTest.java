package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.PlatoRequestDTO;
import com.elsoldorado.app.model.Categoria;
import com.elsoldorado.app.model.Plato;
import com.elsoldorado.app.repository.CategoriaRepository;
import com.elsoldorado.app.repository.PlatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock PlatoRepository platoRepository;
    @Mock CategoriaRepository categoriaRepository;
    MenuService service;

    @BeforeEach void setUp() { service = new MenuService(platoRepository, categoriaRepository); }

    @Test void creaPlatoConCategoriaValida() {
        Categoria categoria = new Categoria(1L, "Entradas");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(platoRepository.save(any(Plato.class))).thenAnswer(i -> i.getArgument(0));
        PlatoRequestDTO request = new PlatoRequestDTO();
        request.setNombre("Ceviche"); request.setPrecio(new BigDecimal("32.00")); request.setCategoriaId(1L); request.setDisponible(true);
        Plato resultado = service.agregarPlato(request);
        assertEquals("Ceviche", resultado.getNombre());
        assertEquals(categoria, resultado.getCategoria());
        verify(platoRepository).save(any(Plato.class));
    }

    @Test void rechazaCategoriaInexistente() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        PlatoRequestDTO request = new PlatoRequestDTO();
        request.setNombre("Plato"); request.setPrecio(BigDecimal.ONE); request.setCategoriaId(99L);
        assertThrows(IllegalArgumentException.class, () -> service.agregarPlato(request));
    }
}
