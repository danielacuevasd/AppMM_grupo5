package com.example.appmm_grupo5.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SeguridadTest {

    @Test
    fun hash_conocido_de_abc() {
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            Seguridad.sha256("abc")
        )
    }

    @Test
    fun la_misma_clave_da_el_mismo_hash() {
        assertEquals(Seguridad.sha256("clave1234"), Seguridad.sha256("clave1234"))
    }

    @Test
    fun claves_distintas_dan_hashes_distintos() {
        assertNotEquals(Seguridad.sha256("clave1234"), Seguridad.sha256("clave1235"))
    }
}