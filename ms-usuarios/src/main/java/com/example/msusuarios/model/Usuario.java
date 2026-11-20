package com.example.msusuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "correo"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String telefono;

    @Column(name = "clave_hash", nullable = false)
    private String claveHash;

    public Usuario() {
    }

    public Usuario(String nombre, String correo, String telefono, String claveHash) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.claveHash = claveHash;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getClaveHash() { return claveHash; }
    public void setClaveHash(String claveHash) { this.claveHash = claveHash; }
}