package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.data.ContenidoDao
import com.example.appmm_grupo5.data.EncuestaDao
import com.example.appmm_grupo5.data.FavoritoDao
import com.example.appmm_grupo5.data.PerfilNegocioDao
import com.example.appmm_grupo5.data.UsuarioDao
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.model.Encuesta
import com.example.appmm_grupo5.model.Favorito
import com.example.appmm_grupo5.model.PerfilNegocio
import com.example.appmm_grupo5.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// DAOs de prueba que para no usar base de datos real
class UsuarioDaoTest : UsuarioDao {
    val usuarios = mutableListOf<Usuario>()

    override suspend fun insertar(usuario: Usuario): Long {
        val id = usuarios.size + 1L
        usuarios.add(usuario.copy(id = id))
        return id
    }

    override suspend fun buscarPorCorreo(correo: String): Usuario? =
        usuarios.firstOrNull { it.correo == correo }

    override suspend fun buscarPorId(id: Long): Usuario? =
        usuarios.firstOrNull { it.id == id }
}

class PerfilNegocioDaoTest : PerfilNegocioDao {
    private val perfiles = mutableMapOf<Long, PerfilNegocio>()

    override suspend fun guardar(perfil: PerfilNegocio) {
        perfiles[perfil.usuarioId] = perfil
    }

    override suspend fun obtener(usuarioId: Long): PerfilNegocio? = perfiles[usuarioId]

    override fun observar(usuarioId: Long): Flow<PerfilNegocio?> = flowOf(perfiles[usuarioId])
}

class EncuestaDaoTest : EncuestaDao {
    private val encuestas = mutableMapOf<Long, Encuesta>()

    override suspend fun guardar(encuesta: Encuesta) {
        encuestas[encuesta.usuarioId] = encuesta
    }

    override suspend fun obtener(usuarioId: Long): Encuesta? = encuestas[usuarioId]

    override fun observar(usuarioId: Long): Flow<Encuesta?> = flowOf(encuestas[usuarioId])
}

class ContenidoDaoTest(val contenidos: List<Contenido>) : ContenidoDao {
    override suspend fun insertarTodos(contenidos: List<Contenido>) {}

    override suspend fun contar(): Int = contenidos.size

    override fun filtrar(tipo: String?, categoria: String?): Flow<List<Contenido>> =
        flowOf(
            contenidos.filter {
                (tipo == null || it.tipo == tipo) && (categoria == null || it.categoria == categoria)
            }.sortedBy { it.titulo }
        )

    override suspend fun buscarPorId(id: Long): Contenido? =
        contenidos.firstOrNull { it.id == id }
}

class FavoritoDaoTest(private val contenidos: List<Contenido>) : FavoritoDao {
    private val favoritos = mutableSetOf<Favorito>()

    override suspend fun agregar(favorito: Favorito) {
        favoritos.add(favorito)
    }

    override suspend fun quitar(usuarioId: Long, contenidoId: Long) {
        favoritos.remove(Favorito(usuarioId, contenidoId))
    }

    override fun esFavorito(usuarioId: Long, contenidoId: Long): Flow<Boolean> =
        flowOf(Favorito(usuarioId, contenidoId) in favoritos)

    override fun idsFavoritos(usuarioId: Long): Flow<List<Long>> =
        flowOf(favoritos.filter { it.usuarioId == usuarioId }.map { it.contenidoId })

    override fun observarFavoritos(usuarioId: Long): Flow<List<Contenido>> {
        val ids = favoritos.filter { it.usuarioId == usuarioId }.map { it.contenidoId }
        return flowOf(contenidos.filter { it.id in ids }.sortedBy { it.titulo })
    }
}