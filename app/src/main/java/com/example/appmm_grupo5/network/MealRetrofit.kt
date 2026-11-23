package com.example.appmm_grupo5.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Cliente de la API externa. Es independiente del RetrofitClient de los microservicios
object MealRetrofit {
    // Debe terminar en "/"
    private const val URL_BASE = "https://www.themealdb.com/api/json/v1/1/"

    val api: MealApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MealApiService::class.java)
    }
}