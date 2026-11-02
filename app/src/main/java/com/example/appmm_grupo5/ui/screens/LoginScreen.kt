package com.example.appmm_grupo5.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.R
import com.example.appmm_grupo5.ui.components.BotonPrincipal
import com.example.appmm_grupo5.ui.components.CampoTexto
import com.example.appmm_grupo5.ui.components.DialogoErrores
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import com.example.appmm_grupo5.viewmodel.UsuarioViewModel

// Pantalla de ingreso. No tiene reglas: pide al ViewModel que valide y reacciona al resultado.
@Composable
fun LoginScreen(
    viewModel: UsuarioViewModel,
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    onIngresar: ((Boolean) -> Unit) -> Unit = { resultado -> resultado(true) }
) {
    val estado by viewModel.login.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de Master Martini",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Ingresa a tu cuenta",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            CampoTexto(
                valor = estado.correo,
                onValorChange = viewModel::onLoginCorreoChange,
                etiqueta = "Correo",
                iconoInicial = Icons.Filled.Email,
                tipoTeclado = KeyboardType.Email,
                error = estado.errores.correo
            )
            CampoTexto(
                valor = estado.clave,
                onValorChange = viewModel::onLoginClaveChange,
                etiqueta = "Clave",
                iconoInicial = Icons.Filled.Lock,
                esClave = true,
                error = estado.errores.clave
            )

            BotonPrincipal(
                texto = "Ingresar",
                onClick = {
                    if (viewModel.validarLogin()) {
                        onIngresar { correcto ->
                            if (correcto) onLoginExitoso() else viewModel.credencialesIncorrectas()
                        }
                    } else {
                        mostrarDialogo = true
                    }
                }
            )
            TextButton(onClick = onIrARegistro) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }

    if (mostrarDialogo) {
        DialogoErrores(
            errores = estado.errores.mensajes(),
            onCerrar = { mostrarDialogo = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AppMM_grupo5Theme {
        LoginScreen(viewModel = UsuarioViewModel(), onLoginExitoso = {}, onIrARegistro = {})
    }
}