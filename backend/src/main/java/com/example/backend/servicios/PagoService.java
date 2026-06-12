package com.example.backend.servicios;

import com.example.backend.persistencia.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public Double obtenerCajaDelDia(LocalDate fecha) {
        Double total = pagoRepository.sumMontoByFecha(fecha);
        return total != null ? total : 0.0;
    }

    public Double obtenerCajaDelMes(int mes, int anio) {
        Double total = pagoRepository.sumMontoByMesAndAnio(mes, anio);
        return total != null ? total : 0.0;
    }

    public Double obtenerCajaDelaSemana() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);
        Double total = pagoRepository.sumMontoByRango(inicioSemana, finSemana);
        return total != null ? total : 0.0;
    }

    public Map<String, Object> obtenerResumenCompleto() {
        LocalDate hoy = LocalDate.now();
        int mes = hoy.getMonthValue();
        int anio = hoy.getYear();

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalHoy", obtenerCajaDelDia(hoy));
        resumen.put("totalSemana", obtenerCajaDelaSemana());
        resumen.put("totalMes", obtenerCajaDelMes(mes, anio));

        // Conteo por método de pago del mes actual
        List<Object[]> conteos = pagoRepository.countByMetodoPagoEnMes(mes, anio);
        Map<String, Long> metodos = new HashMap<>();
        metodos.put("Efectivo", 0L);
        metodos.put("Tarjeta", 0L);
        metodos.put("Transferencia", 0L);
        for (Object[] row : conteos) {
            String metodo = (String) row[0];
            Long cantidad = (Long) row[1];
            metodos.put(metodo, cantidad);
        }
        resumen.put("conteoMetodos", metodos);

        return resumen;
    }
}

