package com.example.msusuarios.service;

import com.example.msusuarios.dto.ActualizarRequest;
import com.example.msusuarios.dto.LoginRequest;
import com.example.msusuarios.dto.RegistroRequest;
import com.example.msusuarios.dto.UsuarioResponse;
import com.example.msusuarios.model.Usuario;
import com.example.msusuarios.repository.UsuarioRepository;
import com.example.msusuarios.util.Seguridad;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository repositorio;

    public UsuarioService(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    // 409 si el correo ya está registrado
    public UsuarioResponse registrar(RegistroRequest datos) {
        String correo = normalizarCorreo(datos.correo());
        if (repositorio.existsByCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Usuario nuevo = new Usuario(
                datos.nombre().trim(),
                correo,
                datos.telefono().trim(),
                Seguridad.sha256(datos.clave()));
        return aRespuesta(repositorio.save(nuevo));
    }

    // 401 si el correo no existe o la clave no coincide
    public UsuarioResponse iniciarSesion(LoginRequest datos) {
        Usuario usuario = repositorio.findByCorreo(normalizarCorreo(datos.correo()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos"));
        if (!usuario.getClaveHash().equals(Seguridad.sha256(datos.clave()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos");
        }
        return aRespuesta(usuario);
    }

    // 404 si no existe
    public UsuarioResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    public UsuarioResponse actualizar(Long id, ActualizarRequest datos) {
        Usuario usuario = buscar(id);
        usuario.setNombre(datos.nombre().trim());
        usuario.setTelefono(datos.telefono().trim());
        if (datos.clave() != null && !datos.clave().isBlank()) {
            usuario.setClaveHash(Seguridad.sha256(datos.clave()));
        }
        return aRespuesta(repositorio.save(usuario));
    }

    public void eliminar(Long id) {
        if (!repositorio.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        repositorio.deleteById(id);
    }

    private Usuario buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }

    private UsuarioResponse aRespuesta(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono());
    }
}