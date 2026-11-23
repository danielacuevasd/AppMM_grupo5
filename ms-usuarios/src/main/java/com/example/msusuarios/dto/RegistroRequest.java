package com.example.msusuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistroRequest(
        @NotBlank String nombre,
        @NotBlank @Email String correo,
        @NotBlank String telefono,
        @NotBlank String clave) {
}