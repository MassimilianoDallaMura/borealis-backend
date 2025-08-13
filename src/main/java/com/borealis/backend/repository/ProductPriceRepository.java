package com.borealis.backend.repository;

import com.borealis.backend.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    // Questo repository servirà per gestire i record della cronologia dei prezzi
    // Non avremo bisogno di manipolarli direttamente tramite API, dato che sono gestiti da Product
}
