package com.borealis.backend.dto.response;

import lombok.Data; // Lombok per getter/setter/costruttori

import java.util.List;

@Data // Genera getter, setter, equals, hashCode, toString
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer"; // Tipo di token, standard per JWT
    private Long id;
    private String name; // Il nome dell'utente autenticato
    private String email; // L'email dell'utente autenticato
    private List<String> roles; // I ruoli dell'utente

    public JwtResponseDTO(String accessToken, Long id, String name, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }
}
