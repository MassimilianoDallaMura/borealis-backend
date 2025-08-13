package com.borealis.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor; // Aggiunto per un costruttore senza argomenti

import java.util.List;

@Data // Genera getter, setter, equals, hashCode, toString
@NoArgsConstructor // Genera un costruttore senza argomenti, utile per la deserializzazione
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email; // <-- AGGIUNTO: Campo per l'email
    private List<String> roles; // <-- AGGIUNTO: Campo per i ruoli
}
