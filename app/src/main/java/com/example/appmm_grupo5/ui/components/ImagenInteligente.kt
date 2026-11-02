package com.example.appmm_grupo5.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Muestra la imagen en el perfil del negocio
// Si no hay foto se muestra un icono
@Composable
fun ImagenInteligente(
    uri: Uri?,
    modifier: Modifier = Modifier,
    tamano: Dp = 160.dp,
    descripcion: String = "Foto del negocio"
) {
    val contexto = LocalContext.current

    // La imagen se lee fuera del hilo principal y se vuelve a leer solo si cambia el Uri
    val imagen by produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = if (uri == null) null else withContext(Dispatchers.IO) { cargarImagen(contexto, uri) }
    }

    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val bitmap = imagen
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = descripcion,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Sin foto del negocio",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(tamano / 2)
            )
        }
    }
}

// Lee la imagen desde el Uri y la corrige si es necesario
private fun cargarImagen(contexto: Context, uri: Uri, ladoMaximo: Int = 800): ImageBitmap? {
    return try {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contexto.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, medidas) }

        var reduccion = 1
        while (medidas.outWidth / reduccion > ladoMaximo || medidas.outHeight / reduccion > ladoMaximo) {
            reduccion *= 2
        }

        val opciones = BitmapFactory.Options().apply { inSampleSize = reduccion }
        val bitmap = contexto.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        } ?: return null

        val grados = contexto.contentResolver.openInputStream(uri)?.use { leerGiro(it) } ?: 0
        val corregido = if (grados == 0) bitmap else {
            val matriz = Matrix().apply { postRotate(grados.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
        }
        corregido.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

private fun leerGiro(entrada: java.io.InputStream): Int {
    val orientacion = ExifInterface(entrada).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    return when (orientacion) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
}

@Preview(showBackground = true)
@Composable
fun ImagenInteligentePreview() {
    AppMM_grupo5Theme {
        ImagenInteligente(uri = null)
    }
}