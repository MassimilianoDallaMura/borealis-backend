package com.borealis.backend.security;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtKeyGenerator {

    public static void main(String[] args) {
        // Genera 64 byte casuali.
        // Per l'algoritmo HS512, la chiave deve essere di almeno 512 bit (64 byte).
        // Una chiave più lunga aumenta la sicurezza.
        byte[] keyBytes = new byte[64]; // 64 byte = 512 bit
        new SecureRandom().nextBytes(keyBytes); // Riempie l'array con byte casuali sicuri

        // Codifica i byte in una stringa Base64 URL-safe.
        // Questo formato è quello che la libreria JJWT si aspetta per il 'jwtSecret'.
        String secretKey = Base64.getUrlEncoder().encodeToString(keyBytes);

        System.out.println("Chiave JWT Generata (per HS512):");
        System.out.println(secretKey);
        System.out.println("\nCopia questa stringa e incollala nel tuo file application.properties per la proprietà borealis.app.jwtSecret.");
    }
}
