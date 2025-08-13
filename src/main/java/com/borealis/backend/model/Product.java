package com.borealis.backend.model;

import com.borealis.backend.Enum.Gender;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference("user-owned-products")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @JsonBackReference("user-sold-products")
    private User seller;

    @Column(name = "seller_commission_amount")
    private Double sellerCommissionAmount; // Ammontare della commissione del venditore

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference("category-products")
    private Category category;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "size", length = 50)
    private String size;

    @Column(name = "composition", length = 255)
    private String composition;

    @Column(name = "brand", length = 20)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "insertion_date", nullable = false)
    private LocalDate insertionDate;

    @Column(name = "sale_date")
    private LocalDate saleDate;

    @Column(name = "sold", nullable = false)
    private boolean sold = false;

    // Questa è la lista della cronologia dei prezzi. Essenziale!
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("modificationDate DESC")
    @JsonManagedReference("product-prices")
    private List<ProductPrice> priceHistory = new ArrayList<>();

    // Metodo per aggiungere un nuovo prezzo alla cronologia e aggiornare il prezzo corrente del prodotto
    public void addPrice(BigDecimal newPrice) {
        if (this.priceHistory == null) {
            this.priceHistory = new ArrayList<>();
        }
        ProductPrice newPriceRecord = new ProductPrice(this, newPrice, LocalDateTime.now());
        this.priceHistory.add(newPriceRecord);
        this.setCurrentPrice(newPrice);
    }

    // Metodo per marcare il prodotto come venduto
    public void markAsSold(User seller) {
        this.setSold(true);
        this.setSaleDate(LocalDate.now());
        this.setSeller(seller);
    }
}