package com.example.appmm_grupo5.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmm_grupo5.R
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme
import kotlinx.coroutines.delay

// Pantalla de bienvenida: el logo aparece con fundido y rebote, luego el texto sube.
// Al terminar avisa con onTerminar para que la navegación decida a dónde ir.
@Composable
fun SplashScreen(onTerminar: () -> Unit) {
    var iniciado by remember { mutableStateOf(false) }

    // Cuando "iniciado" pasa a true, estos valores se animan solos hasta su destino
    val transparencia by animateFloatAsState(
        targetValue = if (iniciado) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "transparenciaLogo"
    )
    val escala by animateFloatAsState(
        targetValue = if (iniciado) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "escalaLogo"
    )

    LaunchedEffect(Unit) {
        iniciado = true
        delay(2200)
        onTerminar()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de Master Martini",
                modifier = Modifier
                    .width(240.dp)
                    .graphicsLayer {
                        alpha = transparencia
                        scaleX = escala
                        scaleY = escala
                    },
                contentScale = ContentScale.Fit
            )
            AnimatedVisibility(
                visible = iniciado,
                enter = fadeIn(tween(800, delayMillis = 600)) +
                        slideInVertically(tween(800, delayMillis = 600)) { it / 2 }
            ) {
                Text(
                    text = "Master Martini Academia",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    AppMM_grupo5Theme {
        SplashScreen(onTerminar = {})
    }
}