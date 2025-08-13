package com.borealis.backend.dto.request;

import jakarta.validation.constraints.NotNull; // Per la validazione che l'ID non sia nullo

// Importa Lombok per i getter/setter e costruttori, se lo stai usando
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera getter, setter, toString, equals, hashCode
@NoArgsConstructor // Genera un costruttore senza argomenti
@AllArgsConstructor // Genera un costruttore con tutti gli argomenti
public class ProductSaleRequestDTO {
    @NotNull(message = "Seller ID cannot be null")
    private Long sellerId; // L'ID dell'utente che ha venduto il prodotto
}
