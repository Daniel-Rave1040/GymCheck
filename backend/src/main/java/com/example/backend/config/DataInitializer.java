package com.example.backend.config;

import com.example.backend.entidades.Membresia;
import com.example.backend.persistencia.MembresiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MembresiaRepository membresiaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (membresiaRepository.count() == 0) {
            Membresia basico = new Membresia();
            basico.setTipo("Básico");
            basico.setPrecio(80000.0);

            Membresia semiPro = new Membresia();
            semiPro.setTipo("Semi-Pro");
            semiPro.setPrecio(120000.0);

            Membresia premium = new Membresia();
            premium.setTipo("Premium");
            premium.setPrecio(150000.0);

            membresiaRepository.saveAll(Arrays.asList(basico, semiPro, premium));
            System.out.println("Membresías por defecto creadas.");
        }
    }
}
