package com.example.backend;

import com.example.backend.entidades.Ciudad;
import com.example.backend.persistencia.CiudadRepository;
import com.example.backend.servicios.CiudadService;
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
public class TestCiudadService {

    @Mock
    private CiudadRepository ciudadRepository;

    @InjectMocks
    private CiudadService ciudadService;

    // ===================== findAll =====================

    @Test
    public void testObtenerTodas_retornaListaDeCiudades() {
        // DATOS
        Ciudad c1 = new Ciudad();
        c1.setIdCiudad(1);
        c1.setNombre("Medellin");
        List<Ciudad> lista = new ArrayList<>();
        lista.add(c1);
        when(ciudadRepository.findAll()).thenReturn(lista);

        // EJECUTAR
        List<Ciudad> resultado = ciudadService.findAll();

        // VALIDAR
        assertEquals(1, resultado.size());
        assertEquals("Medellin", resultado.get(0).getNombre());
        verify(ciudadRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerTodas_retornaListaVacia() {
        // DATOS
        when(ciudadRepository.findAll()).thenReturn(new ArrayList<>());

        // EJECUTAR
        List<Ciudad> resultado = ciudadService.findAll();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // ===================== findByPais =====================

    @Test
    public void testObtenerPorPais_retornaCiudadesDelPais() {
        // DATOS
        Integer idPais = 5;
        Ciudad c1 = new Ciudad();
        c1.setIdCiudad(1);
        c1.setNombre("Medellin");
        List<Ciudad> lista = new ArrayList<>();
        lista.add(c1);
        when(ciudadRepository.findByPaisIdPais(idPais)).thenReturn(lista);

        // EJECUTAR
        List<Ciudad> resultado = ciudadService.findByPais(idPais);

        // VALIDAR
        assertEquals(1, resultado.size());
        assertEquals("Medellin", resultado.get(0).getNombre());
        verify(ciudadRepository, times(1)).findByPaisIdPais(idPais);
    }

    @Test
    public void testObtenerPorPais_idNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.findByPais(null));
        verify(ciudadRepository, never()).findByPaisIdPais(any());
    }

    @Test
    public void testObtenerPorPais_idNegativo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.findByPais(-1));
        verify(ciudadRepository, never()).findByPaisIdPais(any());
    }

    @Test
    public void testObtenerPorPais_idCero_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.findByPais(0));
        verify(ciudadRepository, never()).findByPaisIdPais(any());
    }

    // ===================== save =====================

    @Test
    public void testCrear_ciudadValida() {
        // DATOS
        Ciudad ciudadGuardar = new Ciudad();
        ciudadGuardar.setNombre("Cali");
        Ciudad ciudadGuardada = new Ciudad();
        ciudadGuardada.setIdCiudad(2);
        ciudadGuardada.setNombre("Cali");
        when(ciudadRepository.save(ciudadGuardar)).thenReturn(ciudadGuardada);

        // EJECUTAR
        Ciudad resultado = ciudadService.save(ciudadGuardar);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(2, resultado.getIdCiudad());
        assertEquals("Cali", resultado.getNombre());
        verify(ciudadRepository, times(1)).save(ciudadGuardar);
    }

    @Test
    public void testCrear_ciudadNula_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.save(null));
        verify(ciudadRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreVacio_lanzaExcepcion() {
        // DATOS
        Ciudad ciudad = new Ciudad();
        ciudad.setNombre("");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.save(ciudad));
        verify(ciudadRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreNulo_lanzaExcepcion() {
        // DATOS
        Ciudad ciudad = new Ciudad();
        ciudad.setNombre(null);

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.save(ciudad));
        verify(ciudadRepository, never()).save(any());
    }

    // ===================== delete =====================

    @Test
    public void testEliminar_idValido() {
        // DATOS
        Integer id = 1;
        doNothing().when(ciudadRepository).deleteById(id);

        // EJECUTAR
        ciudadService.delete(id);

        // VALIDAR
        verify(ciudadRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminar_idNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.delete(null));
        verify(ciudadRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idNegativo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.delete(-3));
        verify(ciudadRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idCero_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> ciudadService.delete(0));
        verify(ciudadRepository, never()).deleteById(any());
    }
}
