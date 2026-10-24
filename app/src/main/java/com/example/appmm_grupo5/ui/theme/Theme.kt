package com.example.appmm_grupo5.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Los botones principales usan verde con texto blanco (alto contraste)
// y el dorado se usa como color secundario con texto verde.
private val EsquemaMarca = lightColorScheme(
    primary = VerdeMartini,
    onPrimary = Blanco,
    primaryContainer = DoradoClaro,
    onPrimaryContainer = VerdeMartini,
    secondary = DoradoMartini,
    onSecondary = VerdeMartini,
    secondaryContainer = DoradoClaro,
    onSecondaryContainer = TextoPrincipal,
    tertiary = VerdeClaro,
    onTertiary = TextoPrincipal,
    background = FondoClaro,
    onBackground = TextoPrincipal,
    surface = Blanco,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieGris,
    onSurfaceVariant = TextoSecundario,
    error = ErrorRojo,
    onError = Blanco
)

// Tema global de la aplicación: se aplica en MainActivity
@Composable
fun AppMM_grupo5Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaMarca,
        typography = Typography,
        content = content
    )
}