package com.borealis.backend.dto.request;

import com.borealis.backend.Enum.Gender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequestDTO {
    @Size(max = 500, message = "La descrizione non può superare i 500 caratteri")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Il prezzo non può essere negativo")
    private BigDecimal currentPrice;

    @Size(max = 50, message = "La taglia non può superare i 50 caratteri")
    private String size;

    @Size(max = 255, message = "La composizione non può superare i 255 caratteri")
    private String composition;

    private Gender gender; // Non @NotNull per permettere aggiornamenti parziali

    private Long ownerId; // <<<<<<<<<<<< AGGIUNTO: Per permettere il cambio di proprietario

    private String brand;

    private Long categoryId; // <<<<<<<<<<<< AGGIUNTO: Per permettere il cambio di categoria

    private Boolean sold; // <<<<<<<<<<<< AGGIUNTO: Per permettere il cambio di stato venduto/disponibile
    // Usiamo Boolean per distinguere tra non fornito e false esplicito
}
