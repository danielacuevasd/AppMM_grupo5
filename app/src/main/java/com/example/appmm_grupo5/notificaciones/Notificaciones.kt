package com.example.appmm_grupo5.notificaciones

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.appmm_grupo5.MainActivity
import com.example.appmm_grupo5.R
import com.example.appmm_grupo5.data.AppDatabase
import com.example.appmm_grupo5.model.Contenido
import kotlinx.coroutines.flow.first

// Notificaciones locales de la app (recurso nativo).
// Se avisa de contenido nuevo cuando el usuario activó "Recibir novedades" en la encuesta.
object Notificaciones {

    const val CANAL_NOVEDADES = "novedades"
    private const val ID_NOVEDAD = 1001

    // Desde Android 13 hay que pedir el permiso; antes solo se revisa que no estén bloqueadas
    fun tienePermiso(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    // Desde Android 8 toda notificación pertenece a un canal que el usuario puede configurar
    private fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(
            CANAL_NOVEDADES,
            "Novedades de contenido",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos de recetas, videos y cursos nuevos"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    // Muestra la notificación. Devuelve false si no hay permiso.
    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, titulo: String, texto: String): Boolean {
        if (!tienePermiso(context)) return false
        crearCanal(context)

        // Al tocar la notificación se abre la app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendiente = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_NOVEDADES)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(ID_NOVEDAD, notificacion)
        return true
    }

    // Elige el primer contenido que coincide con algún interés; si no hay, el primero de la lista
    fun elegirContenido(contenidos: List<Contenido>, intereses: Set<String>): Contenido? =
        contenidos.firstOrNull { it.categoria in intereses } ?: contenidos.firstOrNull()

    // Busca un contenido del catálogo según los intereses y avisa de él
    suspend fun avisarContenidoNuevo(context: Context, intereses: Set<String>): Boolean {
        val base = AppDatabase.obtener(context)
        base.cargarDatosEjemploSiHaceFalta()
        val todos = base.contenidoDao().filtrar(null, null).first()
        val elegido = elegirContenido(todos, intereses) ?: return false
        return mostrar(
            context = context,
            titulo = "Nuevo para ti en Master Martini",
            texto = "${elegido.tipo}: ${elegido.titulo} (${elegido.categoria})"
        )
    }
}