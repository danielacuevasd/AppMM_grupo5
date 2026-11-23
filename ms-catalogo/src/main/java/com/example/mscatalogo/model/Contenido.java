package com.example.mscatalogo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contenidos")
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(name = "duracion_minutos", nullable = false)
    private int duracionMinutos;

    @Column(nullable = false)
    private String nivel;

    public Contenido() {
    }

    public Contenido(String tipo, String titulo, String categoria, String descripcion, int duracionMinutos, String nivel) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.duracionMinutos = duracionMinutos;
        this.nivel = nivel;
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
}
