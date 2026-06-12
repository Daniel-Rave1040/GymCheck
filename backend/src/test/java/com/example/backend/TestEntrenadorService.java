package com.example.backend;

import com.example.backend.entidades.Entrenador;
import com.example.backend.persistencia.EntrenadorRepository;
import com.example.backend.servicios.EntrenadorService;
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
public class TestEntrenadorService {

    @Mock
    private EntrenadorRepository entrenadorRepository;

    @InjectMocks
    private EntrenadorService entrenadorService;

    // ===================== findAll =====================

    @Test
    public void testObtenerTodos_retornaListaDeEntrenadores() {
        // DATOS
        Entrenador e1 = new Entrenador();
        e1.setIdEntrenador(1);
        e1.setNombre("Carlos");
        List<Entrenador> lista = new ArrayList<>();
        lista.add(e1);
        when(entrenadorRepository.findAll()).thenReturn(lista);

        // EJECUTAR
        List<Entrenador> resultado = entrenadorService.findAll();

        // VALIDAR
        assertEquals(1, resultado.size());
        assertEquals("Carlos", resultado.get(0).getNombre());
        verify(entrenadorRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerTodos_retornaListaVacia() {
        // DATOS
        when(entrenadorRepository.findAll()).thenReturn(new ArrayList<>());

        // EJECUTAR
        List<Entrenador> resultado = entrenadorService.findAll();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // ===================== save =====================

    @Test
    public void testCrear_entrenadorValido() {
        // DATOS
        Entrenador entrenadorGuardar = new Entrenador();
        entrenadorGuardar.setNombre("Luis");
        Entrenador entrenadorGuardado = new Entrenador();
        entrenadorGuardado.setIdEntrenador(2);
        entrenadorGuardado.setNombre("Luis");
        when(entrenadorRepository.save(entrenadorGuardar)).thenReturn(entrenadorGuardado);

        // EJECUTAR
        Entrenador resultado = entrenadorService.save(entrenadorGuardar);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(2, resultado.getIdEntrenador());
        assertEquals("Luis", resultado.getNombre());
        verify(entrenadorRepository, times(1)).save(entrenadorGuardar);
    }

    @Test
    public void testCrear_entrenadorNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> entrenadorService.save(null));
        verify(entrenadorRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreVacio_lanzaExcepcion() {
        // DATOS
        Entrenador entrenador = new Entrenador();
        entrenador.setNombre("");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> entrenadorService.save(entrenador));
        verify(entrenadorRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreNulo_lanzaExcepcion() {
        // DATOS
        Entrenador entrenador = new Entrenador();
        entrenador.setNombre(null);

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> entrenadorService.save(entrenador));
        verify(entrenadorRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreSoloEspacios_lanzaExcepcion() {
        // DATOS
        Entrenador entrenador = new Entrenador();
        entrenador.setNombre("   ");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> entrenadorService.save(entrenador));
        verify(entrenadorRepository, never()).save(any());
    }

    // ===================== delete =====================

    @Test
    public void testEliminar_idValido() {
        // DATOS
        Integer id = 1;
        doNothing().when(entrenadorRepository).deleteById(id);

        // EJECUTAR
        entrenadorService.delete(id);

        // VALIDAR
        verify(entrenadorRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminar_idNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> entrenadorService.delete(null));
        verify(entrenadorRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idNegativo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> entrenadorService.delete(-1));
        verify(entrenadorRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idCero_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> entrenadorService.delete(0));
        verify(entrenadorRepository, never()).deleteById(any());
    }
}
