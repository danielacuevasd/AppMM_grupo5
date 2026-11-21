package com.example.mscatalogo.repository;

import com.example.mscatalogo.model.Favorito;
import com.example.mscatalogo.model.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

    List<Favorito> findByUsuarioId(Long usuarioId);
}
