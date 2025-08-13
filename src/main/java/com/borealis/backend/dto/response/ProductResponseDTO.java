package com.borealis.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String description;
    private BigDecimal currentPrice;
    private LocalDate insertionDate;
    private LocalDate saleDate;
    private boolean sold;
    private String size;
    private String brand;
    private String composition;
    private String gender; // Enum convertito in String per l'output

    // Dettagli semplificati delle relazioni
    private UserResponseDTO owner; // Un DTO semplificato per l'utente proprietario
    private UserResponseDTO seller; // Un DTO semplificato per l'utente venditore (se presente)
    private Double sellerCommissionAmount; // Ammontare della commissione
    private CategoryResponseDTO category; // Un DTO semplificato per la categoria

    // Non esponi direttamente la priceHistory a meno che non sia richiesto specificamente da un endpoint
    // private List<ProductPriceResponseDTO> priceHistory;
}