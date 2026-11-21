package com.example.appmm_grupo5.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints de ms-usuarios (puerto 8081)
interface UsuarioApi {
    @POST("api/usuarios")
    suspend fun registrar(@Body datos: RegistroRequest): Response<UsuarioResponse>

    @POST("api/usuarios/login")
    suspend fun login(@Body datos: LoginRequest): Response<UsuarioResponse>

    @GET("api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<UsuarioResponse>

    @PUT("api/usuarios/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body datos: ActualizarRequest): Response<UsuarioResponse>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}