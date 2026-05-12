package com.example.backend.services;

import com.example.backend.models.Cliente;
import com.example.backend.models.ClienteMembresia;
import com.example.backend.models.Membresia;
import com.example.backend.models.Pago;
import com.example.backend.repositories.ClienteMembresiaRepository;
import com.example.backend.repositories.ClienteRepository;
import com.example.backend.repositories.MembresiaRepository;
import com.example.backend.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MembresiaService {

    @Autowired
    private ClienteMembresiaRepository clienteMembresiaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MembresiaRepository membresiaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    public ClienteMembresia comprarMembresia(Integer idCliente, Integer idMembresia) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Membresia membresia = membresiaRepository.findById(idMembresia)
                .orElseThrow(() -> new RuntimeException("Membresia no encontrada"));

        // 1. Crear el registro en ClienteMembresia (+30 días)
        ClienteMembresia cm = new ClienteMembresia();
        cm.setCliente(cliente);
        cm.setMembresia(membresia);
        cm.setFechaInicio(LocalDate.now());
        cm.setFechaFin(LocalDate.now().plusDays(30)); // Regla de negocio de los 30 días
        
        ClienteMembresia nuevaSuscripcion = clienteMembresiaRepository.save(cm);

        // 2. Registrar el Pago automáticamente
        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setMonto(membresia.getPrecio());
        pago.setFecha(LocalDate.now());
        pagoRepository.save(pago);

        return nuevaSuscripcion;
    }

    public long calcularDiasRestantes(Integer idCliente) {
        List<ClienteMembresia> membresias = clienteMembresiaRepository.findMembresiasByClienteIdDesc(idCliente);
        if (membresias.isEmpty()) {
            return 0; // No tiene membresias
        }
        
        // Tomamos la membresía más reciente
        ClienteMembresia actual = membresias.get(0);
        LocalDate hoy = LocalDate.now();
        
        if (actual.getFechaFin().isBefore(hoy)) {
            return 0; // Ya venció
        }
        
        return ChronoUnit.DAYS.between(hoy, actual.getFechaFin());
    }
}
