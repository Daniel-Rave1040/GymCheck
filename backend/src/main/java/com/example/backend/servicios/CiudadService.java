package com.example.backend.servicios;

import com.example.backend.entidades.Ciudad;
import com.example.backend.persistencia.CiudadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiudadService {

    @Autowired
    private CiudadRepository ciudadRepository;

    public List<Ciudad> findAll() {
        return ciudadRepository.findAll();
    }

    public List<Ciudad> findByPais(Integer idPais) {
        if (idPais == null) {
            throw new IllegalArgumentException("El id del país no puede ser nulo");
        }
        if (idPais <= 0) {
            throw new IllegalArgumentException("El id del país debe ser positivo");
        }
        return ciudadRepository.findByPaisIdPais(idPais);
    }

    public Ciudad save(Ciudad ciudad) {
        if (ciudad == null) {
            throw new IllegalArgumentException("La ciudad no puede ser nula");
        }
        if (ciudad.getNombre() == null || ciudad.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ciudad es obligatorio");
        }
        return ciudadRepository.save(ciudad);
    }

    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo");
        }
        ciudadRepository.deleteById(id);
    }
}
