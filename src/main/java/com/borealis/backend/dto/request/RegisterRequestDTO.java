package com.borealis.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Lombok per getter, setter, equals, hashCode, toString
public class RegisterRequestDTO {

    @NotBlank(message = "Il nome utente è obbligatorio")
    @Size(min = 3, max = 20, message = "Il nome utente deve avere tra 3 e 20 caratteri")
    private String name;

    @NotBlank(message = "L'email è obbligatoria")
    @Size(max = 50, message = "L'email non può superare i 50 caratteri")
    @Email(message = "L'email deve essere in un formato valido")
    private String email;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 6, max = 40, message = "La password deve avere tra 6 e 40 caratteri")
    private String password;
}
