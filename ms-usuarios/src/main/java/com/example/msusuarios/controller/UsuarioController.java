package com.example.msusuarios.controller;

import com.example.msusuarios.dto.ActualizarRequest;
import com.example.msusuarios.dto.LoginRequest;
import com.example.msusuarios.dto.RegistroRequest;
import com.example.msusuarios.dto.UsuarioResponse;
import com.example.msusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrar(datos));
    }

    @PostMapping("/login")
    public UsuarioResponse iniciarSesion(@Valid @RequestBody LoginRequest datos) {
        return servicio.iniciarSesion(datos);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarRequest datos) {
        return servicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}