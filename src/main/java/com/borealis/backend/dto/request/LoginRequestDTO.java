package com.borealis.backend.dto.request;

import jakarta.validation.constraints.NotBlank; // Per validazione
import lombok.Data; // Lombok per getter/setter/costruttori

@Data // Genera getter, setter, equals, hashCode, toString
public class LoginRequestDTO {
    @NotBlank(message = "L'email non può essere vuota") // Validazione: l'email non deve essere vuota
    private String email; // Usiamo l'email come username per il login
    @NotBlank(message = "La password non può essere vuota") // Validazione: la password non deve essere vuota
    private String password;
}
