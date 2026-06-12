package com.example.backend;

import com.example.backend.dto.VentaMembresiaRequest;
import com.example.backend.entidades.*;
import com.example.backend.persistencia.*;
import com.example.backend.servicios.MembresiaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMembresiaService {

    @Mock
    private ClienteMembresiaRepository clienteMembresiaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private MembresiaRepository membresiaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private MembresiaService membresiaService;

    // ===================== comprarMembresia =====================

    @Test
    public void testComprarMembresia_clienteExistenteSinMembresiaAnterior() {
        // DATOS
        VentaMembresiaRequest request = new VentaMembresiaRequest();
        request.setDocumentoCliente("12345");
        request.setIdMembresia(1);
        request.setMeses(1);
        request.setMetodoPago("Efectivo");

        Cliente cliente = new Cliente();
        cliente.setIdCliente(10);
        cliente.setDocumento("12345");
        cliente.setNombre("Pedro");

        Membresia membresia = new Membresia();
        membresia.setIdMembresia(1);
        membresia.setTipo("Mensual");
        membresia.setPrecio(50000.0);

        when(clienteRepository.findByDocumento("12345")).thenReturn(Optional.of(cliente));
        when(membresiaRepository.findById(1)).thenReturn(Optional.of(membresia));
        when(clienteMembresiaRepository.findMembresiasByClienteIdDesc(10)).thenReturn(new ArrayList<>());

        ClienteMembresia cmGuardada = new ClienteMembresia();
        cmGuardada.setId(100);
        cmGuardada.setCliente(cliente);
        cmGuardada.setMembresia(membresia);
        cmGuardada.setFechaInicio(LocalDate.now());
        cmGuardada.setFechaFin(LocalDate.now().plusDays(30));
        when(clienteMembresiaRepository.save(any(ClienteMembresia.class))).thenReturn(cmGuardada);

        // EJECUTAR
        ClienteMembresia resultado = membresiaService.comprarMembresia(request);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(100, resultado.getId());
        assertEquals(cliente, resultado.getCliente());
        assertEquals(membresia, resultado.getMembresia());
        verify(clienteRepository, times(1)).findByDocumento("12345");
        verify(membresiaRepository, times(1)).findById(1);
        verify(clienteMembresiaRepository, times(1)).save(any(ClienteMembresia.class));
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    public void testComprarMembresia_clienteNuevo_seCreaNuevo() {
        // DATOS
        VentaMembresiaRequest request = new VentaMembresiaRequest();
        request.setDocumentoCliente("99999");
        request.setNombreCliente("Juan");
        request.setIdMembresia(2);
        request.setMeses(2);
        request.setMetodoPago("Tarjeta");

        Cliente clienteNuevo = new Cliente();
        clienteNuevo.setIdCliente(20);
        clienteNuevo.setDocumento("99999");
        clienteNuevo.setNombre("Juan");

        Membresia membresia = new Membresia();
        membresia.setIdMembresia(2);
        membresia.setTipo("Trimestral");
        membresia.setPrecio(130000.0);

        when(clienteRepository.findByDocumento("99999")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteNuevo);
        when(membresiaRepository.findById(2)).thenReturn(Optional.of(membresia));
        when(clienteMembresiaRepository.findMembresiasByClienteIdDesc(20)).thenReturn(new ArrayList<>());

        ClienteMembresia cmGuardada = new ClienteMembresia();
        cmGuardada.setId(101);
        cmGuardada.setCliente(clienteNuevo);
        cmGuardada.setMembresia(membresia);
        when(clienteMembresiaRepository.save(any(ClienteMembresia.class))).thenReturn(cmGuardada);

        // EJECUTAR
        ClienteMembresia resultado = membresiaService.comprarMembresia(request);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(101, resultado.getId());
        verify(clienteRepository, times(1)).findByDocumento("99999");
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(clienteMembresiaRepository, times(1)).save(any(ClienteMembresia.class));
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    public void testComprarMembresia_clienteConMembresiaActiva_seExtiendeFecha() {
        // DATOS
        VentaMembresiaRequest request = new VentaMembresiaRequest();
        request.setDocumentoCliente("12345");
        request.setIdMembresia(1);
        request.setMeses(1);
        request.setMetodoPago("Efectivo");

        Cliente cliente = new Cliente();
        cliente.setIdCliente(10);
        cliente.setDocumento("12345");

        Membresia membresia = new Membresia();
        membresia.setIdMembresia(1);
        membresia.setPrecio(50000.0);

        LocalDate finFuturo = LocalDate.now().plusDays(10);
        ClienteMembresia activa = new ClienteMembresia();
        activa.setFechaFin(finFuturo);
        List<ClienteMembresia> lista = new ArrayList<>();
        lista.add(activa);

        when(clienteRepository.findByDocumento("12345")).thenReturn(Optional.of(cliente));
        when(membresiaRepository.findById(1)).thenReturn(Optional.of(membresia));
        when(clienteMembresiaRepository.findMembresiasByClienteIdDesc(10)).thenReturn(lista);

        ClienteMembresia cmGuardada = new ClienteMembresia();
        cmGuardada.setFechaInicio(finFuturo);
        cmGuardada.setFechaFin(finFuturo.plusDays(30));
        when(clienteMembresiaRepository.save(any(ClienteMembresia.class))).thenReturn(cmGuardada);

        // EJECUTAR
        ClienteMembresia resultado = membresiaService.comprarMembresia(request);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(finFuturo, resultado.getFechaInicio());
        assertEquals(finFuturo.plusDays(30), resultado.getFechaFin());
        verify(clienteMembresiaRepository, times(1)).save(any(ClienteMembresia.class));
    }

    @Test
    public void testComprarMembresia_membresiaNoEncontrada_lanzaExcepcion() {
        // DATOS
        VentaMembresiaRequest request = new VentaMembresiaRequest();
        request.setDocumentoCliente("12345");
        request.setIdMembresia(99);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(10);
        when(clienteRepository.findByDocumento("12345")).thenReturn(Optional.of(cliente));
        when(membresiaRepository.findById(99)).thenReturn(Optional.empty());

        // EJECUTAR & VALIDAR
        assertThrows(RuntimeException.class, () -> membresiaService.comprarMembresia(request));
        verify(clienteMembresiaRepository, never()).save(any(ClienteMembresia.class));
    }

    // ===================== calcularDiasRestantes =====================

    @Test
    public void testCalcularDiasRestantes_sinMembresias_retornaCero() {
        // DATOS
        Integer idCliente = 10;
        when(clienteMembresiaRepository.findMembresiasByClienteIdDesc(idCliente)).thenReturn(new ArrayList<>());

        // EJECUTAR
        long resultado = membresiaService.calcularDiasRestantes(idCliente);

        // VALIDAR
        assertEquals(0, resultado);
        verify(clienteMembresiaRepository, times(1)).findMembresiasByClienteIdDesc(idCliente);
    }

    @Test
    public void testCalcularDiasRestantes_membresiaVencida_retornaCero() {
        // DATOS
        Integer idCliente = 10;
        ClienteMembresia cm = new ClienteMembresia();
        cm.setFechaFin(LocalDate.now().minusDays(5));
        List<ClienteMembresia> lista = new ArrayList<>();
        lista.add(cm);
        when(clienteMembresiaRepository.findMembresiasByClienteIdDesc(idCliente)).thenReturn(lista);

        // EJECUTAR
        long resultado = membresiaService.calcularDiasRestantes(idCliente);

        // VALIDAR
        assertEquals(0, resultado);
    }

    @Test
    public void testCalcularDiasRestantes_membresiaActiva_retornaDiasRestantes() {
        // DATOS
        Integer idCliente = 10;
        ClienteMembresia cm = new ClienteMembresia();
        cm.setFechaFin(LocalDate.now().plusDays(15));
        List<ClienteMembresia> lista = new ArrayList<>();
        lista.add(cm);
        when(clienteMembresiaRepository.findMembresiasByClienteIdDesc(idCliente)).thenReturn(lista);

        // EJECUTAR
        long resultado = membresiaService.calcularDiasRestantes(idCliente);

        // VALIDAR
        assertEquals(15, resultado);
    }

    // ===================== calcularDiasRestantesPorDocumento =====================

    @Test
    public void testCalcularDiasRestantesPorDocumento_clienteNoEncontrado_retornaMenosUno() {
        // DATOS
        String documento = "12345";
        when(clienteRepository.findByDocumento(documento)).thenReturn(Optional.empty());

        // EJECUTAR
        long resultado = membresiaService.calcularDiasRestantesPorDocumento(documento);

        // VALIDAR
        assertEquals(-1, resultado);
        verify(clienteRepository, times(1)).findByDocumento(documento);
        verify(clienteMembresiaRepository, never()).findMembresiasByClienteIdDesc(anyInt());
    }

    @Test
    public void testCalcularDiasRestantesPorDocumento_clienteEncontrado_retornaDias() {
        // DATOS
        String documento = "12345";
        Cliente cliente = new Cliente();
        cliente.setIdCliente(10);
        ClienteMembresia cm = new ClienteMembresia();
        cm.setFechaFin(LocalDate.now().plusDays(20));
        List<ClienteMembresia> lista = new ArrayList<>();
        lista.add(cm);
        when(clienteRepository.findByDocumento(documento)).thenReturn(Optional.of(cliente));
        when(clienteMembresiaRepository.findMembresiasByClienteIdDesc(10)).thenReturn(lista);

        // EJECUTAR
        long resultado = membresiaService.calcularDiasRestantesPorDocumento(documento);

        // VALIDAR
        assertEquals(20, resultado);
        verify(clienteRepository, times(1)).findByDocumento(documento);
        verify(clienteMembresiaRepository, times(1)).findMembresiasByClienteIdDesc(10);
    }

    // ===================== findAll =====================

    @Test
    public void testObtenerTodas_retornaListaDeMembresías() {
        // DATOS
        Membresia m1 = new Membresia();
        m1.setIdMembresia(1);
        m1.setTipo("Mensual");
        List<Membresia> lista = new ArrayList<>();
        lista.add(m1);
        when(membresiaRepository.findAll()).thenReturn(lista);

        // EJECUTAR
        List<Membresia> resultado = membresiaService.findAll();

        // VALIDAR
        assertEquals(1, resultado.size());
        assertEquals("Mensual", resultado.get(0).getTipo());
        verify(membresiaRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerTodas_retornaListaVacia() {
        // DATOS
        when(membresiaRepository.findAll()).thenReturn(new ArrayList<>());

        // EJECUTAR
        List<Membresia> resultado = membresiaService.findAll();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }
}
