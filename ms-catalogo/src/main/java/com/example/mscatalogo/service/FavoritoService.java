package com.example.mscatalogo.service;

import com.example.mscatalogo.dto.ContenidoResponse;
import com.example.mscatalogo.dto.FavoritoRequest;
import com.example.mscatalogo.dto.FavoritoResponse;
import com.example.mscatalogo.model.Contenido;
import com.example.mscatalogo.model.Favorito;
import com.example.mscatalogo.model.FavoritoId;
import com.example.mscatalogo.repository.ContenidoRepository;
import com.example.mscatalogo.repository.FavoritoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritos;
    private final ContenidoRepository contenidos;

    public FavoritoService(FavoritoRepository favoritos, ContenidoRepository contenidos) {
        this.favoritos = favoritos;
        this.contenidos = contenidos;
    }

    public List<ContenidoResponse> listarDeUsuario(Long usuarioId) {
        List<Long> ids = favoritos.findByUsuarioId(usuarioId).stream()
                .map(Favorito::getContenidoId)
                .toList();
        return contenidos.findAllById(ids).stream()
                .sorted(Comparator.comparing(Contenido::getTitulo))
                .map(this::aRespuesta)
                .toList();
    }

    public FavoritoResponse agregar(FavoritoRequest datos) {
        if (!contenidos.existsById(datos.contenidoId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido no encontrado");
        }
        FavoritoId clave = new FavoritoId(datos.usuarioId(), datos.contenidoId());
        if (!favoritos.existsById(clave)) {
            favoritos.save(new Favorito(datos.usuarioId(), datos.contenidoId()));
        }
        return new FavoritoResponse(datos.usuarioId(), datos.contenidoId());
    }

    public void quitar(Long usuarioId, Long contenidoId) {
        FavoritoId clave = new FavoritoId(usuarioId, contenidoId);
        if (!favoritos.existsById(clave)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El favorito no existe");
        }
        favoritos.deleteById(clave);
    }

    private ContenidoResponse aRespuesta(Contenido c) {
        return new ContenidoResponse(c.getId(), c.getTipo(), c.getTitulo(), c.getCategoria(),
                c.getDescripcion(), c.getDuracionMinutos(), c.getNivel());
    }
}
