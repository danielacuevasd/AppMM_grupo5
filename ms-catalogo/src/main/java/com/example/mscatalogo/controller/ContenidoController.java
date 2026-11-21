package com.example.mscatalogo.controller;

import com.example.mscatalogo.dto.ContenidoRequest;
import com.example.mscatalogo.dto.ContenidoResponse;
import com.example.mscatalogo.service.ContenidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenidos")
public class ContenidoController {

    private final ContenidoService servicio;

    public ContenidoController(ContenidoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ContenidoResponse> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String categoria) {
        return servicio.listar(tipo, categoria);
    }

    @GetMapping("/{id}")
    public ContenidoResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    public ResponseEntity<ContenidoResponse> crear(@Valid @RequestBody ContenidoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    @PutMapping("/{id}")
    public ContenidoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ContenidoRequest datos) {
        return servicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
