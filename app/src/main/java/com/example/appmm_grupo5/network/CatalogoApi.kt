package com.example.appmm_grupo5.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CatalogoApi {
    @GET("api/contenidos")
    suspend fun listar(
        @Query("tipo") tipo: String? = null,
        @Query("categoria") categoria: String? = null
    ): Response<List<ContenidoResponse>>

    @GET("api/contenidos/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<ContenidoResponse>

    @POST("api/contenidos")
    suspend fun crear(@Body datos: ContenidoRequest): Response<ContenidoResponse>

    @PUT("api/contenidos/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body datos: ContenidoRequest): Response<ContenidoResponse>

    @DELETE("api/contenidos/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>

    @GET("api/favoritos/usuario/{usuarioId}")
    suspend fun favoritosDeUsuario(@Path("usuarioId") usuarioId: Long): Response<List<ContenidoResponse>>

    @POST("api/favoritos")
    suspend fun agregarFavorito(@Body datos: FavoritoRequest): Response<FavoritoResponse>

    @DELETE("api/favoritos")
    suspend fun quitarFavorito(
        @Query("usuarioId") usuarioId: Long,
        @Query("contenidoId") contenidoId: Long
    ): Response<Unit>
}
