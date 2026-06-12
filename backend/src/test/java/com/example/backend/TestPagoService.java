package com.example.backend;

import com.example.backend.persistencia.PagoRepository;
import com.example.backend.servicios.PagoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestPagoService {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    // ===================== obtenerCajaDelDia =====================

    @Test
    public void testObtenerCajaDelDia_conPagos() {
        // DATOS
        LocalDate fecha = LocalDate.of(2026, 6, 10);
        Double montoEsperado = 150000.0;
        when(pagoRepository.sumMontoByFecha(fecha)).thenReturn(montoEsperado);

        // EJECUTAR
        Double resultado = pagoService.obtenerCajaDelDia(fecha);

        // VALIDAR
        assertEquals(montoEsperado, resultado);
        verify(pagoRepository, times(1)).sumMontoByFecha(fecha);
    }

    @Test
    public void testObtenerCajaDelDia_sinPagos_retornaCero() {
        // DATOS
        LocalDate fecha = LocalDate.of(2026, 6, 10);
        when(pagoRepository.sumMontoByFecha(fecha)).thenReturn(null);

        // EJECUTAR
        Double resultado = pagoService.obtenerCajaDelDia(fecha);

        // VALIDAR
        assertEquals(0.0, resultado);
        verify(pagoRepository, times(1)).sumMontoByFecha(fecha);
    }

    // ===================== obtenerCajaDelMes =====================

    @Test
    public void testObtenerCajaDelMes_conPagos() {
        // DATOS
        int mes = 6;
        int anio = 2026;
        Double montoEsperado = 1200000.0;
        when(pagoRepository.sumMontoByMesAndAnio(mes, anio)).thenReturn(montoEsperado);

        // EJECUTAR
        Double resultado = pagoService.obtenerCajaDelMes(mes, anio);

        // VALIDAR
        assertEquals(montoEsperado, resultado);
        verify(pagoRepository, times(1)).sumMontoByMesAndAnio(mes, anio);
    }

    @Test
    public void testObtenerCajaDelMes_sinPagos_retornaCero() {
        // DATOS
        when(pagoRepository.sumMontoByMesAndAnio(1, 2026)).thenReturn(null);

        // EJECUTAR
        Double resultado = pagoService.obtenerCajaDelMes(1, 2026);

        // VALIDAR
        assertEquals(0.0, resultado);
    }

    // ===================== obtenerCajaDelaSemana =====================

    @Test
    public void testObtenerCajaDelaSemana_conPagos() {
        // DATOS
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);
        Double montoEsperado = 450000.0;
        when(pagoRepository.sumMontoByRango(inicioSemana, finSemana)).thenReturn(montoEsperado);

        // EJECUTAR
        Double resultado = pagoService.obtenerCajaDelaSemana();

        // VALIDAR
        assertEquals(montoEsperado, resultado);
        verify(pagoRepository, times(1)).sumMontoByRango(inicioSemana, finSemana);
    }

    @Test
    public void testObtenerCajaDelaSemana_sinPagos_retornaCero() {
        // DATOS
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);
        when(pagoRepository.sumMontoByRango(inicioSemana, finSemana)).thenReturn(null);

        // EJECUTAR
        Double resultado = pagoService.obtenerCajaDelaSemana();

        // VALIDAR
        assertEquals(0.0, resultado);
    }

    // ===================== obtenerResumenCompleto =====================

    @Test
    public void testObtenerResumenCompleto_retornaMapaConTotales() {
        // DATOS
        LocalDate hoy = LocalDate.now();
        int mes = hoy.getMonthValue();
        int anio = hoy.getYear();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        Double cajaDia = 100000.0;
        Double cajaSemana = 500000.0;
        Double cajaMes = 2000000.0;

        List<Object[]> conteos = new ArrayList<>();
        conteos.add(new Object[]{"Efectivo", 5L});
        conteos.add(new Object[]{"Tarjeta", 10L});
        conteos.add(new Object[]{"Transferencia", 8L});

        when(pagoRepository.sumMontoByFecha(hoy)).thenReturn(cajaDia);
        when(pagoRepository.sumMontoByRango(inicioSemana, finSemana)).thenReturn(cajaSemana);
        when(pagoRepository.sumMontoByMesAndAnio(mes, anio)).thenReturn(cajaMes);
        when(pagoRepository.countByMetodoPagoEnMes(mes, anio)).thenReturn(conteos);

        // EJECUTAR
        Map<String, Object> resultado = pagoService.obtenerResumenCompleto();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(cajaDia, resultado.get("totalHoy"));
        assertEquals(cajaSemana, resultado.get("totalSemana"));
        assertEquals(cajaMes, resultado.get("totalMes"));

        @SuppressWarnings("unchecked")
        Map<String, Long> metodos = (Map<String, Long>) resultado.get("conteoMetodos");
        assertNotNull(metodos);
        assertEquals(5L, metodos.get("Efectivo"));
        assertEquals(10L, metodos.get("Tarjeta"));
        assertEquals(8L, metodos.get("Transferencia"));

        verify(pagoRepository, times(1)).sumMontoByFecha(hoy);
        verify(pagoRepository, times(1)).sumMontoByRango(inicioSemana, finSemana);
        verify(pagoRepository, times(1)).sumMontoByMesAndAnio(mes, anio);
        verify(pagoRepository, times(1)).countByMetodoPagoEnMes(mes, anio);
    }

    @Test
    public void testObtenerResumenCompleto_repositorioRetornaNulos_usaCero() {
        // DATOS
        LocalDate hoy = LocalDate.now();
        int mes = hoy.getMonthValue();
        int anio = hoy.getYear();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        when(pagoRepository.sumMontoByFecha(hoy)).thenReturn(null);
        when(pagoRepository.sumMontoByRango(inicioSemana, finSemana)).thenReturn(null);
        when(pagoRepository.sumMontoByMesAndAnio(mes, anio)).thenReturn(null);
        when(pagoRepository.countByMetodoPagoEnMes(mes, anio)).thenReturn(new ArrayList<>());

        // EJECUTAR
        Map<String, Object> resultado = pagoService.obtenerResumenCompleto();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0.0, resultado.get("totalHoy"));
        assertEquals(0.0, resultado.get("totalSemana"));
        assertEquals(0.0, resultado.get("totalMes"));
    }
}
