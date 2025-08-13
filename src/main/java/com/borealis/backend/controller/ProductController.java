package com.borealis.backend.controller;

import com.borealis.backend.dto.request.ProductRequestDTO;
import com.borealis.backend.dto.request.ProductUpdateRequestDTO;
import com.borealis.backend.dto.request.ProductSaleRequestDTO;
import com.borealis.backend.dto.response.ProductPriceResponseDTO;
import com.borealis.backend.dto.response.ProductResponseDTO;
import com.borealis.backend.dto.response.ProductStatisticsResponseDTO;
import com.borealis.backend.model.Product;
import com.borealis.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "https://bvm.kaninchenhaus.org", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Creazione Prodotto (POST /api/products)
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productDto) {
        try {
            ProductResponseDTO createdProduct = productService.createProduct(productDto);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Recupera Prodotto per ID (GET /api/products/{id})
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        try {
            ProductResponseDTO product = productService.getProductById(id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Recupera TUTTI i prodotti (GET /api/products) - se lo mantieni
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // MODIFICATO: Recupera prodotti con filtri (GET /api/products/filter)
    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponseDTO>> getFilteredProducts(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        List<ProductResponseDTO> products = productService.getFilteredProducts(description, ownerId, categoryId, status);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Assegna Proprietario (PUT /api/products/{productId}/assign-owner)
    @PutMapping("/{productId}/assign-owner")
    public ResponseEntity<ProductResponseDTO> assignProductToOwner(
            @PathVariable Long productId,
            @RequestParam Long newOwnerId) {
        try {
            ProductResponseDTO updatedProduct = productService.assignProductToOwner(productId, newOwnerId);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Elimina Prodotto (DELETE /api/products/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Marca Prodotto come Venduto (PUT /api/products/{productId}/mark-sold)
    @PutMapping("/{productId}/mark-sold")
    public ResponseEntity<ProductResponseDTO> markProductAsSold(
            @PathVariable Long productId,
            @Valid @RequestBody ProductSaleRequestDTO productSaleRequestDTO) {
        try {
            ProductResponseDTO updatedProduct = productService.markProductAsSold(productId, productSaleRequestDTO.getSellerId());
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Statistiche Prodotti (GET /api/products/statistics)
     * Ora accetta un userId opzionale per filtrare le statistiche per proprietario.
     * @param userId L'ID dell'utente proprietario (opzionale).
     * @return ProductStatisticsResponseDTO.
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProductStatisticsResponseDTO> getProductStatistics(@RequestParam(required = false) Long userId) {
        ProductStatisticsResponseDTO stats = productService.getProductStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Aggiornamento completo/parziale di un prodotto.
     * Corrisponde a PUT /api/products/{id}
     *
     * @param id L'ID del prodotto da aggiornare.
     * @param productDto Il DTO con i dati aggiornati del prodotto.
     * @return Il ProductResponseDTO del prodotto aggiornato.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequestDTO productDto) {
        try {
            ProductResponseDTO updatedProduct = productService.updateProduct(id, productDto);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Recupera cronologia prezzi.
     * Corrisponde a GET /api/products/{id}/price-history
     *
     * @param id L'ID del prodotto.
     * @return Una lista di ProductPriceResponseDTO.
     */
    @GetMapping("/{id}/price-history")
    public ResponseEntity<List<ProductPriceResponseDTO>> getProductPriceHistory(@PathVariable Long id) {
        try {
            List<ProductPriceResponseDTO> priceHistory = productService.getProductPriceHistory(id);
            return new ResponseEntity<>(priceHistory, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}