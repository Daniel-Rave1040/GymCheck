package com.example.backend.services;

import com.example.backend.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
}
