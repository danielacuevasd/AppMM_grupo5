package com.example.appmm_grupo5.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme

// Muestra todos los componentes juntos, con los errores escritos a mano
@Preview(showBackground = true, heightDp = 1100)
@Composable
fun ComponentesFormularioPreview() {
    AppMM_grupo5Theme {
        var nombre by remember { mutableStateOf("") }
        var correo by remember { mutableStateOf("camila@negocio") }
        var clave by remember { mutableStateOf("") }
        var tipo by remember { mutableStateOf("Pastelería") }
        var avisos by remember { mutableStateOf(true) }
        var terminos by remember { mutableStateOf(false) }
        var mostrarDialogo by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto(
                valor = nombre,
                onValorChange = { nombre = it },
                etiqueta = "Nombre",
                iconoInicial = Icons.Filled.Person,
                error = if (nombre.isBlank()) "El nombre es obligatorio" else null
            )
            CampoTexto(
                valor = correo,
                onValorChange = { correo = it },
                etiqueta = "Correo",
                iconoInicial = Icons.Filled.Email,
                tipoTeclado = KeyboardType.Email,
                error = "Ingresa un correo válido, por ejemplo nombre@correo.cl"
            )
            CampoTexto(
                valor = clave,
                onValorChange = { clave = it },
                etiqueta = "Clave",
                iconoInicial = Icons.Filled.Lock,
                esClave = true
            )
            OpcionesRadio(
                titulo = "Tipo de negocio",
                opciones = listOf("Pastelería", "Panadería", "Chocolatería"),
                seleccionada = tipo,
                onSeleccion = { tipo = it }
            )
            InterruptorConTexto(
                titulo = "Novedades",
                descripcion = "Recibir contenido nuevo por correo",
                activo = avisos,
                onCambio = { avisos = it }
            )
            AceptarTerminos(
                aceptado = terminos,
                onCambio = { terminos = it },
                error = if (!terminos) "Debes aceptar los términos y la política de privacidad" else null
            )
            BotonPrincipal(texto = "Crear cuenta", onClick = { mostrarDialogo = true })
        }

        if (mostrarDialogo) {
            DialogoErrores(
                errores = listOf("El nombre es obligatorio", "Debes aceptar los términos"),
                onCerrar = { mostrarDialogo = false }
            )
        }
    }
}