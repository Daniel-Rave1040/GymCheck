package com.example.backend.persistencia;

import com.example.backend.entidades.ClienteMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteMembresiaRepository extends JpaRepository<ClienteMembresia, Integer> {

    @Query("SELECT cm FROM ClienteMembresia cm WHERE cm.cliente.idCliente = :idCliente ORDER BY cm.fechaFin DESC")
    List<ClienteMembresia> findMembresiasByClienteIdDesc(@Param("idCliente") Integer idCliente);
}

