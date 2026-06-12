package com.example.backend;

import com.example.backend.entidades.Sede;
import com.example.backend.persistencia.SedeRepository;
import com.example.backend.servicios.SedeService;
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
public class TestSedeService {

    @Mock
    private SedeRepository sedeRepository;

    @InjectMocks
    private SedeService sedeService;

    // ===================== findAll =====================

    @Test
    public void testObtenerTodas_retornaListaDeSedes() {
        // DATOS
        Sede s1 = new Sede();
        s1.setIdSede(1);
        s1.setNombre("Sede Norte");
        List<Sede> lista = new ArrayList<>();
        lista.add(s1);
        when(sedeRepository.findAll()).thenReturn(lista);

        // EJECUTAR
        List<Sede> resultado = sedeService.findAll();

        // VALIDAR
        assertEquals(1, resultado.size());
        assertEquals("Sede Norte", resultado.get(0).getNombre());
        verify(sedeRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerTodas_retornaListaVacia() {
        // DATOS
        when(sedeRepository.findAll()).thenReturn(new ArrayList<>());

        // EJECUTAR
        List<Sede> resultado = sedeService.findAll();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // ===================== save =====================

    @Test
    public void testCrear_sedeValida() {
        // DATOS
        Sede sedeGuardar = new Sede();
        sedeGuardar.setNombre("Sede Sur");
        Sede sedeGuardada = new Sede();
        sedeGuardada.setIdSede(2);
        sedeGuardada.setNombre("Sede Sur");
        when(sedeRepository.save(sedeGuardar)).thenReturn(sedeGuardada);

        // EJECUTAR
        Sede resultado = sedeService.save(sedeGuardar);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(2, resultado.getIdSede());
        assertEquals("Sede Sur", resultado.getNombre());
        verify(sedeRepository, times(1)).save(sedeGuardar);
    }

    @Test
    public void testCrear_sedeNula_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> sedeService.save(null));
        verify(sedeRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreVacio_lanzaExcepcion() {
        // DATOS
        Sede sede = new Sede();
        sede.setNombre("");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> sedeService.save(sede));
        verify(sedeRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreNulo_lanzaExcepcion() {
        // DATOS
        Sede sede = new Sede();
        sede.setNombre(null);

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> sedeService.save(sede));
        verify(sedeRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreSoloEspacios_lanzaExcepcion() {
        // DATOS
        Sede sede = new Sede();
        sede.setNombre("   ");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> sedeService.save(sede));
        verify(sedeRepository, never()).save(any());
    }

    // ===================== delete =====================

    @Test
    public void testEliminar_idValido() {
        // DATOS
        Integer id = 1;
        doNothing().when(sedeRepository).deleteById(id);

        // EJECUTAR
        sedeService.delete(id);

        // VALIDAR
        verify(sedeRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminar_idNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> sedeService.delete(null));
        verify(sedeRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idNegativo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> sedeService.delete(-2));
        verify(sedeRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idCero_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> sedeService.delete(0));
        verify(sedeRepository, never()).deleteById(any());
    }
}
