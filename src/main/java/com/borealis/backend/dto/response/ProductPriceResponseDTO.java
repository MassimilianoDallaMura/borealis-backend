package com.borealis.backend.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductPriceResponseDTO {
    private Long id;
    private BigDecimal price;
    private LocalDateTime modificationDate;
    // Non includere il riferimento al Product per evitare loop di serializzazione e dati ridondanti
}