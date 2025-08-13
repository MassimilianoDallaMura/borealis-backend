package com.borealis.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserCreationRequestDTO {
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    // Campo per specificare i ruoli quando un admin crea un utente
    private Set<String> roles; // Sarà usato per assegnare ROLE_USER o ROLE_SUPERUSER
}
