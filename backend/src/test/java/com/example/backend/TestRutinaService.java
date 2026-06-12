package com.example.backend;

import com.example.backend.entidades.Rutina;
import com.example.backend.persistencia.RutinaRepository;
import com.example.backend.servicios.RutinaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestRutinaService {

    @Mock
    private RutinaRepository rutinaRepository;

    @InjectMocks
    private RutinaService rutinaService;

    // ===================== findAll =====================

    @Test
    public void testObtenerTodas_retornaListaDeRutinas() {
        // DATOS
        Rutina r1 = new Rutina();
        r1.setIdRutina(1);
        r1.setNombre("Hipertrofia");
        List<Rutina> lista = new ArrayList<>();
        lista.add(r1);
        when(rutinaRepository.findAll()).thenReturn(lista);

        // EJECUTAR
        List<Rutina> resultado = rutinaService.findAll();

        // VALIDAR
        assertEquals(1, resultado.size());
        assertEquals("Hipertrofia", resultado.get(0).getNombre());
        verify(rutinaRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerTodas_retornaListaVacia() {
        // DATOS
        when(rutinaRepository.findAll()).thenReturn(new ArrayList<>());

        // EJECUTAR
        List<Rutina> resultado = rutinaService.findAll();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // ===================== save =====================

    @Test
    public void testCrear_rutinaValida() {
        // DATOS
        Rutina rutinaGuardar = new Rutina();
        rutinaGuardar.setNombre("Fuerza");
        Rutina rutinaGuardada = new Rutina();
        rutinaGuardada.setIdRutina(2);
        rutinaGuardada.setNombre("Fuerza");
        when(rutinaRepository.save(rutinaGuardar)).thenReturn(rutinaGuardada);

        // EJECUTAR
        Rutina resultado = rutinaService.save(rutinaGuardar);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(2, resultado.getIdRutina());
        assertEquals("Fuerza", resultado.getNombre());
        verify(rutinaRepository, times(1)).save(rutinaGuardar);
    }

    @Test
    public void testCrear_rutinaNula_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.save(null));
        verify(rutinaRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreVacio_lanzaExcepcion() {
        // DATOS
        Rutina rutina = new Rutina();
        rutina.setNombre("");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.save(rutina));
        verify(rutinaRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreNulo_lanzaExcepcion() {
        // DATOS
        Rutina rutina = new Rutina();
        rutina.setNombre(null);

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.save(rutina));
        verify(rutinaRepository, never()).save(any());
    }

    // ===================== delete =====================

    @Test
    public void testEliminar_idValido() {
        // DATOS
        Integer id = 1;
        doNothing().when(rutinaRepository).deleteById(id);

        // EJECUTAR
        rutinaService.delete(id);

        // VALIDAR
        verify(rutinaRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminar_idNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.delete(null));
        verify(rutinaRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idNegativo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.delete(-1));
        verify(rutinaRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idCero_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.delete(0));
        verify(rutinaRepository, never()).deleteById(any());
    }

    // ===================== update =====================

    @Test
    public void testActualizar_rutinaExistente() {
        // DATOS
        Integer id = 1;
        Rutina rutinaExistente = new Rutina();
        rutinaExistente.setIdRutina(id);
        rutinaExistente.setNombre("Cardio");
        rutinaExistente.setDescripcion("Rutina suave");

        Rutina datosNuevos = new Rutina();
        datosNuevos.setNombre("Cardio Intenso");
        datosNuevos.setDescripcion("Rutina fuerte");

        Rutina rutinaActualizada = new Rutina();
        rutinaActualizada.setIdRutina(id);
        rutinaActualizada.setNombre("Cardio Intenso");
        rutinaActualizada.setDescripcion("Rutina fuerte");

        when(rutinaRepository.findById(id)).thenReturn(Optional.of(rutinaExistente));
        when(rutinaRepository.save(any(Rutina.class))).thenReturn(rutinaActualizada);

        // EJECUTAR
        Rutina resultado = rutinaService.update(id, datosNuevos);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals("Cardio Intenso", resultado.getNombre());
        assertEquals("Rutina fuerte", resultado.getDescripcion());
        verify(rutinaRepository, times(1)).findById(id);
        verify(rutinaRepository, times(1)).save(any(Rutina.class));
    }

    @Test
    public void testActualizar_rutinaNoExiste_lanzaExcepcion() {
        // DATOS
        Integer id = 999;
        Rutina datosNuevos = new Rutina();
        datosNuevos.setNombre("Nueva");
        when(rutinaRepository.findById(id)).thenReturn(Optional.empty());

        // EJECUTAR & VALIDAR
        assertThrows(RuntimeException.class, () -> rutinaService.update(id, datosNuevos));
        verify(rutinaRepository, never()).save(any());
    }

    @Test
    public void testActualizar_idNulo_lanzaExcepcion() {
        // DATOS
        Rutina datosNuevos = new Rutina();
        datosNuevos.setNombre("Algo");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.update(null, datosNuevos));
        verify(rutinaRepository, never()).findById(any());
    }

    @Test
    public void testActualizar_datosNulos_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.update(1, null));
        verify(rutinaRepository, never()).findById(any());
    }

    @Test
    public void testActualizar_idNegativo_lanzaExcepcion() {
        // DATOS
        Rutina datosNuevos = new Rutina();
        datosNuevos.setNombre("Algo");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> rutinaService.update(-5, datosNuevos));
        verify(rutinaRepository, never()).findById(any());
    }
}
