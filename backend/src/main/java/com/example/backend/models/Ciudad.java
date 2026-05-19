package com.example.backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "ciudad")
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciudad")
    private Integer idCiudad;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_pais")
    private Pais pais;

    public Ciudad() {}

    public Integer getIdCiudad() {
         return idCiudad;
    }
    public void setIdCiudad(Integer idCiudad) {
         this.idCiudad = idCiudad; 
    }
    public String getNombre() {
         return nombre; 
    }
    public void setNombre(String nombre) {
         this.nombre = nombre; 
    }
    public Pais getPais() {
         return pais;
    }
    public void setPais(Pais pais) {
         this.pais = pais; 
    }
}
