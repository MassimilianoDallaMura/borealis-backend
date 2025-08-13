package com.borealis.backend.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

@Data
public class UserRequestDTO { // Rinominato da UserCreateRequestDTO
    @NotBlank(message = "Il nome dell'utente è obbligatorio")
    @Size(max = 255, message = "Il nome non può superare i 255 caratteri")
    private String name;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email deve essere in un formato valido")
    @Size(max = 255, message = "L'email non può superare i 255 caratteri")
    private String email;
}