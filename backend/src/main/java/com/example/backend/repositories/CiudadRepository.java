package com.example.backend.repositories;

import com.example.backend.models.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {
    List<Ciudad> findByPaisIdPais(Integer idPais);
}
