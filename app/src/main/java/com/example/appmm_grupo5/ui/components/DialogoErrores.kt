package com.example.appmm_grupo5.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Ventana que lista los errores del formulario cuando se intenta enviar con datos inválidos
@Composable
fun DialogoErrores(
    errores: List<String>,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
        title = { Text("Revisa el formulario") },
        text = {
            Column {
                errores.forEach { mensaje ->
                    Text(text = "• $mensaje", modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Entendido") }
        }
    )
}