package com.example.backend.services;

import com.example.backend.models.Ciudad;
import com.example.backend.repositories.CiudadRepository;
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
        return ciudadRepository.findByPaisIdPais(idPais);
    }

    public Ciudad save(Ciudad ciudad) {
        return ciudadRepository.save(ciudad);
    }
}
