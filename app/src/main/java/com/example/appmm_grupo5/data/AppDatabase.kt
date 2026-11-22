package com.example.appmm_grupo5.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appmm_grupo5.model.Encuesta
import com.example.appmm_grupo5.model.PerfilNegocio

@Database(
    entities = [PerfilNegocio::class, Encuesta::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun perfilNegocioDao(): PerfilNegocioDao
    abstract fun encuestaDao(): EncuestaDao

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