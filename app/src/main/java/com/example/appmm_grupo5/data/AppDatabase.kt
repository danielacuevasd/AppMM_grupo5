package com.example.appmm_grupo5.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.model.Encuesta
import com.example.appmm_grupo5.model.Favorito
import com.example.appmm_grupo5.model.PerfilNegocio
import com.example.appmm_grupo5.model.Usuario

@Database(
    entities = [Usuario::class, PerfilNegocio::class, Encuesta::class, Contenido::class, Favorito::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun perfilNegocioDao(): PerfilNegocioDao
    abstract fun encuestaDao(): EncuestaDao
    abstract fun contenidoDao(): ContenidoDao
    abstract fun favoritoDao(): FavoritoDao

    // Carga el contenido de ejemplo solo la primera vez, cuando la tabla está vacía
    suspend fun cargarDatosEjemploSiHaceFalta() {
        if (contenidoDao().contar() == 0) {
            contenidoDao().insertarTodos(DatosEjemplo.contenidos)
        }
    }

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        // Una sola base de datos para toda la app
        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "master_martini.db"
                )
                    // Mientras desarrollamos: si cambia el esquema, se recrea la base
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
    }
}