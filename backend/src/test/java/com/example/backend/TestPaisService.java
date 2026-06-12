package com.example.backend;

import com.example.backend.entidades.Pais;
import com.example.backend.persistencia.PaisRepository;
import com.example.backend.servicios.PaisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestPaisService {

    @Mock
    private PaisRepository paisRepository;

    @InjectMocks
    private PaisService paisService;

    // ===================== findAll =====================

    @Test
    public void testObtenerTodos_retornaListaDePaises() {
        // DATOS
        Pais p1 = new Pais();
        p1.setIdPais(1);
        p1.setNombre("Colombia");
        List<Pais> lista = new ArrayList<>();
        lista.add(p1);
        when(paisRepository.findAll()).thenReturn(lista);

        // EJECUTAR
        List<Pais> resultado = paisService.findAll();

        // VALIDAR
        assertEquals(1, resultado.size());
        assertEquals("Colombia", resultado.get(0).getNombre());
        verify(paisRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerTodos_retornaListaVacia() {
        // DATOS
        when(paisRepository.findAll()).thenReturn(new ArrayList<>());

        // EJECUTAR
        List<Pais> resultado = paisService.findAll();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // ===================== save =====================

    @Test
    public void testCrear_paisValido() {
        // DATOS
        Pais paisGuardar = new Pais();
        paisGuardar.setNombre("Argentina");
        Pais paisGuardado = new Pais();
        paisGuardado.setIdPais(2);
        paisGuardado.setNombre("Argentina");
        when(paisRepository.save(paisGuardar)).thenReturn(paisGuardado);

        // EJECUTAR
        Pais resultado = paisService.save(paisGuardar);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(2, resultado.getIdPais());
        assertEquals("Argentina", resultado.getNombre());
        verify(paisRepository, times(1)).save(paisGuardar);
    }

    @Test
    public void testCrear_paisNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> paisService.save(null));
        verify(paisRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreVacio_lanzaExcepcion() {
        // DATOS
        Pais pais = new Pais();
        pais.setNombre("");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> paisService.save(pais));
        verify(paisRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreSoloEspacios_lanzaExcepcion() {
        // DATOS
        Pais pais = new Pais();
        pais.setNombre("   ");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> paisService.save(pais));
        verify(paisRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreNulo_lanzaExcepcion() {
        // DATOS
        Pais pais = new Pais();
        pais.setNombre(null);

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> paisService.save(pais));
        verify(paisRepository, never()).save(any());
    }

    // ===================== delete =====================

    @Test
    public void testEliminar_idValido() {
        // DATOS
        Integer id = 1;
        doNothing().when(paisRepository).deleteById(id);

        // EJECUTAR
        paisService.delete(id);

        // VALIDAR
        verify(paisRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminar_idNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> paisService.delete(null));
        verify(paisRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idNegativo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> paisService.delete(-1));
        verify(paisRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idCero_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> paisService.delete(0));
        verify(paisRepository, never()).deleteById(any());
    }
}
