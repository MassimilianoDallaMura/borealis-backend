package com.borealis.backend.dto.request; // Dichiarazione del package

import java.math.BigDecimal; // Import necessari

import com.borealis.backend.Enum.Gender; // <-- Controlla che questo import sia corretto e che Gender.java sia nel package Enum
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Data
public class ProductRequestDTO { // <-- Questa è la parentesi graffa di apertura della classe
    @NotBlank(message = "La descrizione non può essere vuota")
    @Size(max = 300, message = "La descrizione non può superare i 500 caratteri")
    private String description;

    @NotNull(message = "Il prezzo corrente è obbligatorio")
    @Min(value = 0, message = "Il prezzo non può essere negativo")
    private BigDecimal currentPrice;

    @NotNull(message = "L'ID del proprietario è obbligatorio")
    private Long ownerId;

    @NotNull(message = "L'ID della categoria è obbligatorio")
    private Long categoryId;

    @Size(max = 5, message = "La taglia non può superare i 50 caratteri")
    private String size;

    @Size(max = 20, message = "Il brand non può superare i 220 caratteri")
    private String brand;

    @Size(max = 100, message = "La composizione non può superare i 255 caratteri")
    private String composition;

    @NotNull(message = "Il genere è obbligatorio")
    private Gender gender;
}