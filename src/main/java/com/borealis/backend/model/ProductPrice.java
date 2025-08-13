package com.borealis.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // IMPORTANTE: NON DIMENTICARE QUESTA LINEA
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor // <-- Mantienilo
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Questa relazione ha bisogno del JsonBackReference per evitare loop di serializzazione
    // e per gestire i proxy di lazy loading.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference("product-prices") // <-- ASSICURATI CHE CI SIA E ABBIA IL NOME CORRETTO
    private Product product;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "modification_date", nullable = false)
    private LocalDateTime modificationDate;

    // Questo è il costruttore che viene cercato dalla classe Product
    public ProductPrice(Product product, BigDecimal price, LocalDateTime modificationDate) {
        this.product = product;
        this.price = price;
        this.modificationDate = modificationDate;
    }
}