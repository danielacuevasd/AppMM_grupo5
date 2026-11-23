package com.example.msusuarios.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Igual que Seguridad.sha256 de la app. MVP académico: en un producto real
// se usaría sal por usuario y bcrypt o Argon2.
public final class Seguridad {

    private Seguridad() {
    }

    public static String sha256(String texto) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}