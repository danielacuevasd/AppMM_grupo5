package com.example.appmm_grupo5.data

import com.example.appmm_grupo5.model.Contenido
import com.example.appmm_grupo5.model.TipoContenido

// Contenido de demostración para el catálogo (datos ficticios)
object DatosEjemplo {
    val contenidos = listOf(
        Contenido(tipo = TipoContenido.RECETA, titulo = "Sablés de cacao", categoria = "Galletas",
            descripcion = "Galletas de mantequilla y cacao con textura arenosa, ideales para vitrina.",
            duracionMinutos = 60, nivel = "Básico"),
        Contenido(tipo = TipoContenido.RECETA, titulo = "Galletas de avena y miel", categoria = "Galletas",
            descripcion = "Receta simple de galletas suaves con avena y un toque de miel.",
            duracionMinutos = 40, nivel = "Básico"),
        Contenido(tipo = TipoContenido.CURSO, titulo = "Templado de chocolate", categoria = "Chocolatería",
            descripcion = "Aprende a controlar la temperatura para lograr brillo y quiebre perfecto.",
            duracionMinutos = 40, nivel = "Intermedio"),
        Contenido(tipo = TipoContenido.RECETA, titulo = "Mousse de chocolate", categoria = "Chocolatería",
            descripcion = "Postre aireado de chocolate semiamargo, listo para porciones individuales.",
            duracionMinutos = 45, nivel = "Básico"),
        Contenido(tipo = TipoContenido.RECETA, titulo = "Croissant de mantequilla", categoria = "Masas de hoja",
            descripcion = "Laminado paso a paso para obtener capas finas y un horneado dorado.",
            duracionMinutos = 180, nivel = "Avanzado"),
        Contenido(tipo = TipoContenido.VIDEO, titulo = "Plegado de hojaldre", categoria = "Masas de hoja",
            descripcion = "Técnica de plegado simple y doble para capas parejas.",
            duracionMinutos = 15, nivel = "Intermedio"),
        Contenido(tipo = TipoContenido.CURSO, titulo = "Pastelería de vitrina", categoria = "Pastelería",
            descripcion = "Cursos de bases, rellenos y montaje para una vitrina atractiva.",
            duracionMinutos = 90, nivel = "Intermedio"),
        Contenido(tipo = TipoContenido.RECETA, titulo = "Torta tres leches", categoria = "Pastelería",
            descripcion = "Bizcocho húmedo con tres leches y una cobertura ligera.",
            duracionMinutos = 75, nivel = "Básico"),
        Contenido(tipo = TipoContenido.VIDEO, titulo = "Crema pastelera sin grumos", categoria = "Pastelería",
            descripcion = "Consejos para cocinar y enfriar la crema sin que se corte.",
            duracionMinutos = 12, nivel = "Básico"),
        Contenido(tipo = TipoContenido.RECETA, titulo = "Helado de vainilla", categoria = "Heladería",
            descripcion = "Base cremosa de vainilla para servir en copa o cono.",
            duracionMinutos = 50, nivel = "Básico"),
        Contenido(tipo = TipoContenido.CURSO, titulo = "Pan de masa madre", categoria = "Horneo",
            descripcion = "Mantención del cultivo, fermentación y horneo en casa o en local.",
            duracionMinutos = 120, nivel = "Avanzado"),
        Contenido(tipo = TipoContenido.RECETA, titulo = "Empanadas de pino al horno", categoria = "Empanadas",
            descripcion = "Masa resistente y relleno jugoso, con consejos de cocción uniforme.",
            duracionMinutos = 90, nivel = "Intermedio"),
        Contenido(tipo = TipoContenido.RECETA, titulo = "Brioche", categoria = "Bollería",
            descripcion = "Masa enriquecida con mantequilla y huevo para pan dulce suave.",
            duracionMinutos = 150, nivel = "Intermedio"),
        Contenido(tipo = TipoContenido.VIDEO, titulo = "Moldeado de bollería", categoria = "Bollería",
            descripcion = "Formas básicas para presentar pan dulce de manera uniforme.",
            duracionMinutos = 10, nivel = "Básico")
    )
}