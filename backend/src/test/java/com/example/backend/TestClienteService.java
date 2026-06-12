package com.example.backend;

import com.example.backend.entidades.Cliente;
import com.example.backend.persistencia.ClienteRepository;
import com.example.backend.servicios.ClienteService;
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
public class TestClienteService {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    // ===================== findAll =====================

    @Test
    public void testObtenerTodos_retornaListaConClientes() {
        // DATOS
        Cliente c1 = new Cliente();
        c1.setIdCliente(1);
        c1.setNombre("Juan");
        Cliente c2 = new Cliente();
        c2.setIdCliente(2);
        c2.setNombre("Maria");
        List<Cliente> listaClientes = new ArrayList<>();
        listaClientes.add(c1);
        listaClientes.add(c2);
        when(clienteRepository.findAll()).thenReturn(listaClientes);

        // EJECUTAR
        List<Cliente> resultado = clienteService.findAll();

        // VALIDAR
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Maria", resultado.get(1).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerTodos_retornaListaVacia() {
        // DATOS
        when(clienteRepository.findAll()).thenReturn(new ArrayList<>());

        // EJECUTAR
        List<Cliente> resultado = clienteService.findAll();

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    // ===================== buscarPorNombre =====================

    @Test
    public void testBuscarPorNombre_encuentraClienteExistente() {
        // DATOS
        String nombreBuscar = "juan";
        Cliente c1 = new Cliente();
        c1.setIdCliente(1);
        c1.setNombre("Juan");
        List<Cliente> listaClientes = new ArrayList<>();
        listaClientes.add(c1);
        when(clienteRepository.findByNombreContainingIgnoreCase(nombreBuscar)).thenReturn(listaClientes);

        // EJECUTAR
        List<Cliente> resultado = clienteService.buscarPorNombre(nombreBuscar);

        // VALIDAR
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        verify(clienteRepository, times(1)).findByNombreContainingIgnoreCase(nombreBuscar);
    }

    @Test
    public void testBuscarPorNombre_sinResultados() {
        // DATOS
        when(clienteRepository.findByNombreContainingIgnoreCase("zzz")).thenReturn(new ArrayList<>());

        // EJECUTAR
        List<Cliente> resultado = clienteService.buscarPorNombre("zzz");

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // ===================== save =====================

    @Test
    public void testCrear_clienteValido() {
        // DATOS
        Cliente clienteGuardar = new Cliente();
        clienteGuardar.setNombre("Pedro");
        clienteGuardar.setApellido("Perez");
        clienteGuardar.setDocumento("12345");

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setIdCliente(10);
        clienteGuardado.setNombre("Pedro");
        clienteGuardado.setApellido("Perez");
        clienteGuardado.setDocumento("12345");

        when(clienteRepository.save(clienteGuardar)).thenReturn(clienteGuardado);

        // EJECUTAR
        Cliente resultado = clienteService.save(clienteGuardar);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals(10, resultado.getIdCliente());
        assertEquals("Pedro", resultado.getNombre());
        verify(clienteRepository, times(1)).save(clienteGuardar);
    }

    @Test
    public void testCrear_clienteNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> clienteService.save(null));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    public void testCrear_nombreVacio_lanzaExcepcion() {
        // DATOS
        Cliente cliente = new Cliente();
        cliente.setNombre("");
        cliente.setApellido("Perez");
        cliente.setDocumento("12345");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> clienteService.save(cliente));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    public void testCrear_apellidoNulo_lanzaExcepcion() {
        // DATOS
        Cliente cliente = new Cliente();
        cliente.setNombre("Pedro");
        cliente.setApellido(null);
        cliente.setDocumento("12345");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> clienteService.save(cliente));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    public void testCrear_documentoVacio_lanzaExcepcion() {
        // DATOS
        Cliente cliente = new Cliente();
        cliente.setNombre("Pedro");
        cliente.setApellido("Perez");
        cliente.setDocumento("   ");

        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> clienteService.save(cliente));
        verify(clienteRepository, never()).save(any());
    }

    // ===================== delete =====================

    @Test
    public void testEliminar_idValido() {
        // DATOS
        Integer idEliminar = 1;
        doNothing().when(clienteRepository).deleteById(idEliminar);

        // EJECUTAR
        clienteService.delete(idEliminar);

        // VALIDAR
        verify(clienteRepository, times(1)).deleteById(idEliminar);
    }

    @Test
    public void testEliminar_idNulo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> clienteService.delete(null));
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idNegativo_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> clienteService.delete(-5));
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    public void testEliminar_idCero_lanzaExcepcion() {
        // EJECUTAR & VALIDAR
        assertThrows(IllegalArgumentException.class, () -> clienteService.delete(0));
        verify(clienteRepository, never()).deleteById(any());
    }

    // ===================== update =====================

    @Test
    public void testActualizar_clienteExistente() {
        // DATOS
        Integer id = 1;
        Cliente clienteExistente = new Cliente();
        clienteExistente.setIdCliente(id);
        clienteExistente.setNombre("Carlos");
        clienteExistente.setApellido("Gomez");
        clienteExistente.setDocumento("12345");
        clienteExistente.setTelefono("99999");

        Cliente datosNuevos = new Cliente();
        datosNuevos.setNombre("Carlos Andres");
        datosNuevos.setApellido("Gomez Perez");
        datosNuevos.setDocumento("54321");
        datosNuevos.setTelefono("88888");

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setIdCliente(id);
        clienteActualizado.setNombre("Carlos Andres");
        clienteActualizado.setApellido("Gomez Perez");
        clienteActualizado.setDocumento("54321");
        clienteActualizado.setTelefono("88888");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActualizado);

        // EJECUTAR
        Cliente resultado = clienteService.update(id, datosNuevos);

        // VALIDAR
        assertNotNull(resultado);
        assertEquals("Carlos Andres", resultado.getNombre());
        assertEquals("Gomez Perez", resultado.getApellido());
        assertEquals("54321", resultado.getDocumento());
        assertEquals("88888", resultado.getTelefono());
        verify(clienteRepository, times(1)).findById(id);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    public void testActualizar_clienteNoExiste_lanzaExcepcion() {
        // DATOS
        Integer id = 999;
        Cliente datosNuevos = new Cliente();
        datosNuevos.setNombre("Nadie");
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // EJECUTAR & VALIDAR
        assertThrows(RuntimeException.class, () -> clienteService.update(id, datosNuevos));
        verify(clienteRepository, never()).save(any());
    }
}
