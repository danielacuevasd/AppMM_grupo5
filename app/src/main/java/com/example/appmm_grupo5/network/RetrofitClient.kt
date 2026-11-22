package com.example.appmm_grupo5.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Un solo cliente HTTP para toda la app
object RetrofitClient {
    // 10.0.2.2 es el computador visto desde el emulador (en celular real hay que usar la IP del PC)
    const val URL_USUARIOS = "http://10.0.2.2:8081/"
    const val URL_CATALOGO = "http://10.0.2.2:8082/"

    private val http = OkHttpClient.Builder()
        // BASIC solo muestra método y código, así no se imprimen las claves en el Logcat
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val usuarioApi: UsuarioApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_USUARIOS)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuarioApi::class.java)
    }

    val catalogoApi: CatalogoApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_CATALOGO)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogoApi::class.java)
    }
}