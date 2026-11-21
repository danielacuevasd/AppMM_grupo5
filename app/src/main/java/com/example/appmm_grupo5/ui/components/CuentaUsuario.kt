package com.example.appmm_grupo5.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.repository.ResultadoUsuario
import com.example.appmm_grupo5.validation.Validadores

// Sección "Mi cuenta": editar datos y eliminar la cuenta (CRUD contra ms-usuarios)
@Composable
fun CuentaUsuario(
    nombre: String,
    telefono: String,
    onActualizar: (String, String, String?, (ResultadoUsuario) -> Unit) -> Unit,
    onEliminar: ((ResultadoUsuario) -> Unit) -> Unit,
    onMensaje: (String) -> Unit
) {
    var mostrarEditar by remember { mutableStateOf(false) }
    var mostrarEliminar by remember { mutableStateOf(false) }

    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoTelefono by remember { mutableStateOf("") }
    var nuevaClave by remember { mutableStateOf("") }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    OutlinedButton(
        onClick = {
            // Se abre el diálogo con los datos actuales
            nuevoNombre = nombre
            nuevoTelefono = telefono
            nuevaClave = ""
            errorNombre = null
            errorTelefono = null
            errorClave = null
            mostrarEditar = true
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("Editar mi cuenta")
    }

    OutlinedButton(
        onClick = { mostrarEliminar = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("Eliminar mi cuenta", color = MaterialTheme.colorScheme.error)
    }

    if (mostrarEditar) {
        AlertDialog(
            onDismissRequest = { mostrarEditar = false },
            title = { Text("Editar cuenta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CampoTexto(
                        valor = nuevoNombre,
                        onValorChange = { nuevoNombre = it },
                        etiqueta = "Tu nombre",
                        iconoInicial = Icons.Filled.Person,
                        error = errorNombre
                    )
                    CampoTexto(
                        valor = nuevoTelefono,
                        onValorChange = { nuevoTelefono = it },
                        etiqueta = "Celular",
                        iconoInicial = Icons.Filled.Phone,
                        tipoTeclado = KeyboardType.Phone,
                        error = errorTelefono
                    )
                    CampoTexto(
                        valor = nuevaClave,
                        onValorChange = { nuevaClave = it },
                        etiqueta = "Nueva clave (opcional)",
                        iconoInicial = Icons.Filled.Lock,
                        esClave = true,
                        error = errorClave
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    errorNombre = Validadores.nombre(nuevoNombre)
                    errorTelefono = Validadores.telefono(nuevoTelefono)
                    // La clave solo se valida si el usuario escribió una nueva
                    errorClave = if (nuevaClave.isBlank()) null else Validadores.clave(nuevaClave)
                    if (errorNombre == null && errorTelefono == null && errorClave == null) {
                        onActualizar(nuevoNombre, nuevoTelefono, nuevaClave.ifBlank { null }) { resultado ->
                            when (resultado) {
                                is ResultadoUsuario.Exito -> {
                                    mostrarEditar = false
                                    onMensaje("Cuenta actualizada")
                                }
                                ResultadoUsuario.ErrorRed ->
                                    onMensaje("No se pudo conectar con el servidor")
                                else -> onMensaje("No se pudo actualizar la cuenta")
                            }
                        }
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEditar = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarEliminar = false },
            title = { Text("Eliminar cuenta") },
            text = { Text("Se borrará tu cuenta de forma permanente. ¿Quieres continuar?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarEliminar = false
                    onEliminar { resultado ->
                        // Si se eliminó, la navegación al login la hace el NavGraph
                        when (resultado) {
                            ResultadoUsuario.Eliminado -> Unit
                            ResultadoUsuario.ErrorRed ->
                                onMensaje("No se pudo conectar con el servidor")
                            else -> onMensaje("No se pudo eliminar la cuenta")
                        }
                    }
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}