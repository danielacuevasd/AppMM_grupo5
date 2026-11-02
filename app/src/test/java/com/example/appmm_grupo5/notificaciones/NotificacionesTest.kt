package com.example.appmm_grupo5.notificaciones

import com.example.appmm_grupo5.data.DatosEjemplo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificacionesTest {

    @Test
    fun elige_un_contenido_de_la_categoria_de_interes() {
        val elegido = Notificaciones.elegirContenido(DatosEjemplo.contenidos, setOf("Galletas"))
        assertEquals("Galletas", elegido?.categoria)
    }

    @Test
    fun sin_coincidencia_elige_el_primer_contenido() {
        val elegido = Notificaciones.elegirContenido(DatosEjemplo.contenidos, emptySet())
        assertEquals(DatosEjemplo.contenidos.first(), elegido)
    }

    @Test
    fun sin_contenidos_no_hay_nada_que_avisar() {
        assertNull(Notificaciones.elegirContenido(emptyList(), setOf("Galletas")))
    }
}