package com.borealis.backend.dto.request;

import jakarta.validation.constraints.NotBlank; // Per la validazione
import jakarta.validation.constraints.Size;   // Per la validazione
import lombok.Data; // Lombok per getter, setter, costruttori

@Data // Genera getter, setter, equals, hashCode, toString
public class CategoryRequestDTO {

    @NotBlank(message = "Il nome della categoria non può essere vuoto")
    @Size(min = 2, max = 50, message = "Il nome della categoria deve avere tra 2 e 50 caratteri")
    private String name;

    // Se la tua categoria avesse altri campi che possono essere inviati nella richiesta,
    // li aggiungeresti qui (es. private String description;).
    // Per ora, basandoci sulla tua entità Category, solo il nome è rilevante.
}
