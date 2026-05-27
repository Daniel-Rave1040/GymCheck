package com.example.backend.services;

import com.example.backend.models.Cliente;
import com.example.backend.models.ClienteMembresia;
import com.example.backend.models.Membresia;
import com.example.backend.models.Pago;
import com.example.backend.dto.VentaMembresiaRequest;
import com.example.backend.repositories.ClienteMembresiaRepository;
import com.example.backend.repositories.ClienteRepository;
import com.example.backend.repositories.MembresiaRepository;
import com.example.backend.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ClienteMembresia comprarMembresia(VentaMembresiaRequest request) {
        // 1. Buscar o Crear Cliente
        Cliente cliente = null;
        if (request.getDocumentoCliente() != null && !request.getDocumentoCliente().isEmpty()) {
            cliente = clienteRepository.findByDocumento(request.getDocumentoCliente()).orElse(null);
        }
        
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setNombre(request.getNombreCliente());
            // Si el frontend envía nombre con apellido junto o separado, por simplicidad guardamos todo en nombre o verificamos si hay espacio.
            // Para mantenerlo simple:
            cliente.setApellido(""); 
            cliente.setDocumento(request.getDocumentoCliente());
            cliente = clienteRepository.save(cliente);
        }

        Membresia membresia = membresiaRepository.findById(request.getIdMembresia())
                .orElseThrow(() -> new RuntimeException("Membresia no encontrada"));

        Integer meses = request.getMeses();
        if (meses == null || meses < 1) meses = 1;

        // 2. Calcular fechas
        LocalDate fechaInicio = LocalDate.now();
        List<ClienteMembresia> membresiasAnteriores = clienteMembresiaRepository.findMembresiasByClienteIdDesc(cliente.getIdCliente());
        if (!membresiasAnteriores.isEmpty()) {
            ClienteMembresia actual = membresiasAnteriores.get(0);
            if (!actual.getFechaFin().isBefore(fechaInicio)) {
                fechaInicio = actual.getFechaFin(); // Si tiene activa, se suma a partir de esa fecha
            }
        }

        // 3. Crear el registro en ClienteMembresia
        ClienteMembresia cm = new ClienteMembresia();
        cm.setCliente(cliente);
        cm.setMembresia(membresia);
        cm.setFechaInicio(fechaInicio);
        cm.setFechaFin(fechaInicio.plusDays(30L * meses));
        
        ClienteMembresia nuevaSuscripcion = clienteMembresiaRepository.save(cm);

        // 4. Registrar el Pago automáticamente
        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setMonto(membresia.getPrecio() * meses);
        pago.setFecha(LocalDate.now());
        pago.setMetodoPago(request.getMetodoPago());
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

    public List<Membresia> findAll() {
        return membresiaRepository.findAll();
    }
}
