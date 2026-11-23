package com.example.mscatalogo.controller;

import com.example.mscatalogo.dto.ContenidoResponse;
import com.example.mscatalogo.dto.FavoritoRequest;
import com.example.mscatalogo.dto.FavoritoResponse;
import com.example.mscatalogo.service.FavoritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService servicio;

    public FavoritoController(FavoritoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ContenidoResponse> listarDeUsuario(@PathVariable Long usuarioId) {
        return servicio.listarDeUsuario(usuarioId);
    }

    @PostMapping
    public ResponseEntity<FavoritoResponse> agregar(@Valid @RequestBody FavoritoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.agregar(datos));
    }

    @DeleteMapping
    public ResponseEntity<Void> quitar(@RequestParam Long usuarioId, @RequestParam Long contenidoId) {
        servicio.quitar(usuarioId, contenidoId);
        return ResponseEntity.noContent().build();
    }
}
