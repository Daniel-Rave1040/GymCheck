package com.example.backend.repositories;

import com.example.backend.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.fecha = :fecha")
    Double sumMontoByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE EXTRACT(MONTH FROM p.fecha) = :mes AND EXTRACT(YEAR FROM p.fecha) = :anio")
    Double sumMontoByMesAndAnio(@Param("mes") int mes, @Param("anio") int anio);
}
