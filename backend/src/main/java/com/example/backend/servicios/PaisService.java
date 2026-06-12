package com.example.backend.servicios;

import com.example.backend.entidades.Pais;
import com.example.backend.persistencia.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaisService {

    @Autowired
    private PaisRepository paisRepository;

    public List<Pais> findAll() {
        return paisRepository.findAll();
    }

    public Pais save(Pais pais) {
        if (pais == null) {
            throw new IllegalArgumentException("El país no puede ser nulo");
        }
        if (pais.getNombre() == null || pais.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio");
        }
        return paisRepository.save(pais);
    }

    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo");
        }
        paisRepository.deleteById(id);
    }
}
