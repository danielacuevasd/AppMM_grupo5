package com.example.appmm_grupo5.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Respuesta de TheMealDB: "meals" es null cuando no hay resultados (no una lista vacía)
data class MealListaResponse(val meals: List<MealDto>?)

// Todos los campos menos idMeal son opcionales porque la API no siempre los envía
data class MealDto(
    val idMeal: String,
    val strMeal: String?,
    val strMealThumb: String?,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?
)

// API externa (terceros): solo lectura, sin control del contrato
interface MealApiService {
    // Lista de postres: filter.php?c=Dessert
    @GET("filter.php")
    suspend fun postres(@Query("c") categoria: String): Response<MealListaResponse>

    // Detalle de un postre: lookup.php?i={id}
    @GET("lookup.php")
    suspend fun detalle(@Query("i") id: String): Response<MealListaResponse>
}