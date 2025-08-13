package com.borealis.backend.security.jwt;

import com.borealis.backend.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders; // Importa Decoders
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${borealis.app.jwtSecret}") // La chiave segreta per firmare il JWT (da application.properties)
    private String jwtSecret;

    @Value("${borealis.app.jwtExpirationMs}") // Tempo di scadenza del JWT (da application.properties)
    private int jwtExpirationMs;

    /**
     * Genera un JWT basato sull'autenticazione dell'utente.
     * @param authentication L'oggetto Authentication di Spring Security.
     * @return Il JWT generato come stringa.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Usiamo l'email come subject del token
                .claim("id", userPrincipal.getId()) // Aggiungi l'ID dell'utente come claim personalizzato
                .claim("name", userPrincipal.getName()) // Aggiungi il nome dell'utente come claim personalizzato
                .setIssuedAt(new Date()) // Data di emissione del token
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Data di scadenza del token
                .signWith(key(), SignatureAlgorithm.HS512) // Firma il token con la chiave segreta usando HS512
                .compact(); // Costruisce il JWT
    }

    /**
     * Ottiene la chiave di firma dal segreto JWT.
     * @return La chiave di firma.
     */
    private Key key() {
        // CORREZIONE CRUCIALE: Usa Decoders.BASE64URL.decode() per decodificare la chiave URL-safe
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }

    /**
     * Estrae lo username (email) dal JWT.
     * @param token Il JWT.
     * @return Lo username (email) estratto.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Valida il JWT.
     * @param authToken Il JWT da validare.
     * @return true se il token è valido, false altrimenti.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
