package com.example.backend.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "entrenador")
public class Entrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrenador")
    private Integer idEntrenador;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "especialidad", length = 50)
    private String especialidad;

    @ManyToOne
    @JoinColumn(name = "id_sede")
    private Sede sede;

    public Entrenador() {}

    public Integer getIdEntrenador() { return idEntrenador; }
    public void setIdEntrenador(Integer idEntrenador) { this.idEntrenador = idEntrenador; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }
}

