package com.example.mscatalogo.repository;

import com.example.mscatalogo.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<Contenido, Long> {

    @Query("SELECT c FROM Contenido c WHERE (:tipo IS NULL OR c.tipo = :tipo) AND (:categoria IS NULL OR c.categoria = :categoria) ORDER BY c.titulo")
    List<Contenido> filtrar(@Param("tipo") String tipo, @Param("categoria") String categoria);
}
