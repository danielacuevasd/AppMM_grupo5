package com.example.mscatalogo.service;

import com.example.mscatalogo.dto.ContenidoRequest;
import com.example.mscatalogo.dto.ContenidoResponse;
import com.example.mscatalogo.model.Contenido;
import com.example.mscatalogo.repository.ContenidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ContenidoService {

    private final ContenidoRepository repositorio;

    public ContenidoService(ContenidoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<ContenidoResponse> listar(String tipo, String categoria) {
        return repositorio.filtrar(vacioANulo(tipo), vacioANulo(categoria)).stream()
                .map(this::aRespuesta)
                .toList();
    }

    public ContenidoResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    public ContenidoResponse crear(ContenidoRequest datos) {
        Contenido nuevo = new Contenido(
                datos.tipo().trim(),
                datos.titulo().trim(),
                datos.categoria().trim(),
                datos.descripcion().trim(),
                datos.duracionMinutos(),
                datos.nivel().trim());
        return aRespuesta(repositorio.save(nuevo));
    }

    public ContenidoResponse actualizar(Long id, ContenidoRequest datos) {
        Contenido contenido = buscar(id);
        contenido.setTipo(datos.tipo().trim());
        contenido.setTitulo(datos.titulo().trim());
        contenido.setCategoria(datos.categoria().trim());
        contenido.setDescripcion(datos.descripcion().trim());
        contenido.setDuracionMinutos(datos.duracionMinutos());
        contenido.setNivel(datos.nivel().trim());
        return aRespuesta(repositorio.save(contenido));
    }

    public void eliminar(Long id) {
        if (!repositorio.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido no encontrado");
        }
        repositorio.deleteById(id);
    }

    private Contenido buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido no encontrado"));
    }

    private String vacioANulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private ContenidoResponse aRespuesta(Contenido c) {
        return new ContenidoResponse(c.getId(), c.getTipo(), c.getTitulo(), c.getCategoria(),
                c.getDescripcion(), c.getDuracionMinutos(), c.getNivel());
    }
}
