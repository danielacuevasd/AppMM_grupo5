package com.example.msusuarios.dto;

// Nunca se devuelve la clave ni su hash
public record UsuarioResponse(Long id, String nombre, String correo, String telefono) {
}