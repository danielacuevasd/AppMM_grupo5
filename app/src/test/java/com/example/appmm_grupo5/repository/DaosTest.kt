package com.example.appmm_grupo5.repository

import com.example.appmm_grupo5.data.EncuestaDao
import com.example.appmm_grupo5.data.PerfilNegocioDao
import com.example.appmm_grupo5.model.Encuesta
import com.example.appmm_grupo5.model.PerfilNegocio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
