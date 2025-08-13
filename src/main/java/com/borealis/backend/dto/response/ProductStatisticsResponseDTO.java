package com.borealis.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Importa BigDecimal per una maggiore precisione con la valuta

@Data // Questa annotazione genera getter, setter, toString, equals e hashCode
@NoArgsConstructor // Genera un costruttore senza argomenti
@AllArgsConstructor // Genera un costruttore con tutti gli argomenti
public class ProductStatisticsResponseDTO {
    private long totalItems;
    private long soldItems;
    private long availableItems;
    private BigDecimal totalRevenue;
    private BigDecimal potentialRevenue;
}