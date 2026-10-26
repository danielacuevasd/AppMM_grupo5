package com.example.appmm_grupo5.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

// Campo de texto reutilizable. No guarda el texto ni valida nada:
// recibe el valor y el mensaje de error desde fuera (ViewModel).
// Animaciones: el mensaje de error entra y sale suavemente y el campo se sacude
// cuando aparece un error nuevo.
@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    iconoInicial: ImageVector? = null,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false
) {
    var claveVisible by remember { mutableStateOf(false) }

    // Se recuerda el último mensaje para que el texto no desaparezca de golpe
    // mientras la animación de salida todavía se está ejecutando
    var ultimoError by remember { mutableStateOf("") }
    SideEffect { if (error != null) ultimoError = error }

    // Desplazamiento horizontal para la sacudida (en píxeles)
    val desplazamiento = remember { Animatable(0f) }
    LaunchedEffect(error) {
        if (error != null) {
            repeat(2) {
                desplazamiento.animateTo(10f, tween(50))
                desplazamiento.animateTo(-10f, tween(50))
            }
            desplazamiento.animateTo(0f, tween(50))
        } else {
            desplazamiento.snapTo(0f)
        }
    }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(desplazamiento.value.roundToInt(), 0) },
        label = { Text(etiqueta) },
        singleLine = true,
        isError = error != null,
        leadingIcon = iconoInicial?.let { icono -> { Icon(icono, contentDescription = null) } },
        trailingIcon = {
            if (esClave) {
                IconButton(onClick = { claveVisible = !claveVisible }) {
                    Icon(
                        imageVector = if (claveVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (claveVisible) "Ocultar clave" else "Mostrar clave"
                    )
                }
            } else if (error != null) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        supportingText = {
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
            ) {
                Text(error ?: ultimoError)
            }
        },
        visualTransformation = if (esClave && !claveVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else tipoTeclado)
    )
}