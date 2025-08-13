package com.borealis.backend.service;

import com.borealis.backend.dto.request.ProductRequestDTO;
import com.borealis.backend.dto.request.ProductUpdateRequestDTO;
import com.borealis.backend.dto.response.ProductPriceResponseDTO;
import com.borealis.backend.dto.response.ProductResponseDTO;
import com.borealis.backend.dto.response.ProductStatisticsResponseDTO;
import com.borealis.backend.model.Category;
import com.borealis.backend.model.Product;
import com.borealis.backend.model.ProductPrice;
import com.borealis.backend.model.User;
import com.borealis.backend.repository.CategoryRepository;
import com.borealis.backend.repository.ProductRepository;
import com.borealis.backend.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;


    private static final double SELLER_PERCENTAGE = 0.10; // 10%

    @Autowired
    public ProductService(ProductRepository productRepository, UserRepository userRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }


    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productDto) {
        User owner = userRepository.findById(productDto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Proprietario (User) con ID " + productDto.getOwnerId() + " non trovato."));
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria con ID " + productDto.getCategoryId() + " non trovata."));

        Product product = modelMapper.map(productDto, Product.class); // ModelMapper mappa già 'brand' qui
        product.setOwner(owner);
        product.setCategory(category);
        // RIMUOVI LA SEGUENTE LINEA: product.setBrand(product.getBrand());

        if (product.getInsertionDate() == null) {
            product.setInsertionDate(LocalDate.now());
        }
        product.setSold(false);

        if (product.getCurrentPrice() != null && (product.getPriceHistory() == null || product.getPriceHistory().isEmpty())) {
            product.addPrice(product.getCurrentPrice());
        }

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto con ID " + id + " non trovato."));
        return modelMapper.map(product, ProductResponseDTO.class);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Recupera i prodotti filtrandoli dinamicamente.
     * Corretto l'errore "not effectively final" riorganizzando la Specification.
     */
    public List<ProductResponseDTO> getFilteredProducts(String description, Long ownerId, Long categoryId, String status) {
        Boolean isSold;
        if (status != null) {
            if ("SOLD".equalsIgnoreCase(status)) {
                isSold = true;
            } else if ("AVAILABLE".equalsIgnoreCase(status)) {
                isSold = false;
            } else {
                isSold = null;
            }
        } else {
            isSold = null;
        }

        // Creazione dinamica della Specification con un'unica lambda
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (description != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if (ownerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("owner").get("id"), ownerId));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (isSold != null) {
                predicates.add(criteriaBuilder.equal(root.get("sold"), isSold));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec).stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDTO assignProductToOwner(Long productId, Long newOwnerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto con ID " + productId + " non trovato."));
        User newOwner = userRepository.findById(newOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("Nuovo proprietario (User) con ID " + newOwnerId + " non trovato."));
        product.setOwner(newOwner);
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponseDTO.class);
    }

    @Transactional
    public ProductResponseDTO markProductAsSold(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto con ID " + productId + " non trovato."));

        if (product.isSold()) {
            throw new IllegalArgumentException("Il prodotto con ID " + productId + " è già stato venduto.");
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Venditore (User) con ID " + sellerId + " non trovato."));

        // Calcolo della commissione del venditore
        // Usa BigDecimal per il calcolo dei soldi per evitare problemi di floating-point
        BigDecimal currentPrice = product.getCurrentPrice();
        BigDecimal commissionRate = BigDecimal.valueOf(SELLER_PERCENTAGE);
        BigDecimal sellerCommission = currentPrice.multiply(commissionRate);

        // Imposta la commissione nel prodotto prima di salvarlo
        product.setSellerCommissionAmount(sellerCommission.doubleValue()); // Converti in Double se il campo è Double

        product.markAsSold(seller); // Questo metodo imposta sold=true, saleDate e seller
        // NOTA: il metodo markAsSold nell'entità Product non imposta sellerCommissionAmount.
        // Lo facciamo qui direttamente prima di salvare.

        Product soldProduct = productRepository.save(product);
        return modelMapper.map(soldProduct, ProductResponseDTO.class);
    }

    /**
     * Recupera le statistiche dei prodotti, opzionalmente filtrate per utente.
     * @param userId L'ID dell'utente per cui recuperare le statistiche. Se non fornito, recupera le statistiche totali.
     * @return ProductStatisticsResponseDTO.
     */
    public ProductStatisticsResponseDTO getProductStatistics(Long userId) {
        List<Product> products;
        if (userId != null) {
            // Usa il metodo findByOwnerId dal repository
            products = productRepository.findByOwnerId(userId);
        } else {
            products = productRepository.findAll();
        }

        long totalItems = products.size();
        long soldItems = products.stream().filter(Product::isSold).count();
        long availableItems = totalItems - soldItems;
        BigDecimal totalRevenue = products.stream()
                .filter(Product::isSold)
                .map(Product::getCurrentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcolo dell'incasso potenziale (somma dei prezzi dei prodotti disponibili)
        BigDecimal potentialRevenue = products.stream()
                .filter(product -> !product.isSold())
                .map(Product::getCurrentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // NOTA: Per restituire anche potentialRevenue, è necessario
        // aggiungere un campo 'potentialRevenue' al DTO ProductStatisticsResponseDTO.
        // Esempio:
        // return new ProductStatisticsResponseDTO(totalItems, soldItems, availableItems, totalRevenue, potentialRevenue);

        return new ProductStatisticsResponseDTO(totalItems, soldItems, availableItems, potentialRevenue, totalRevenue);
    }

    /**
     * Questo metodo accetta ProductUpdateRequestDTO e aggiorna solo i campi forniti.
     * Gestisce anche l'aggiornamento del prezzo e la cronologia.
     * @param productId L'ID del prodotto da aggiornare.
     * @param productDto Il DTO con i dati aggiornati del prodotto.
     * @return Il ProductResponseDTO del prodotto aggiornato.
     * @throws IllegalArgumentException Se il prodotto non viene trovato.
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductUpdateRequestDTO productDto) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto con ID " + productId + " non trovato per l'aggiornamento."));

        // Aggiorna solo i campi che sono forniti (non null) nel DTO
        if (productDto.getDescription() != null) {
            existingProduct.setDescription(productDto.getDescription());
        }
        if (productDto.getSize() != null) {
            existingProduct.setSize(productDto.getSize());
        }
        if (productDto.getComposition() != null) {
            existingProduct.setComposition(productDto.getComposition());
        }
        if (productDto.getGender() != null) {
            existingProduct.setGender(productDto.getGender());
        }

        // Logica per aggiornare il prezzo e la cronologia
        if (productDto.getCurrentPrice() != null) {
            // Se il prezzo è fornito e diverso dal prezzo corrente, aggiungi alla cronologia
            if (existingProduct.getCurrentPrice() == null || existingProduct.getCurrentPrice().compareTo(productDto.getCurrentPrice()) != 0) {
                existingProduct.addPrice(productDto.getCurrentPrice());
            }
        }

        // Gestione di ownerId
        if (productDto.getOwnerId() != null) {
            User newOwner = userRepository.findById(productDto.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Proprietario con ID " + productDto.getOwnerId() + " non trovato."));
            existingProduct.setOwner(newOwner);
        }

        // Gestione di categoryId
        if (productDto.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria con ID " + productDto.getCategoryId() + " non trovata."));
            existingProduct.setCategory(newCategory);
        }

        // Gestione dello stato 'sold'
        if (productDto.getSold() != null) {
            existingProduct.setSold(productDto.getSold());
            // Se il prodotto viene marcato come non venduto dopo essere stato venduto,
            // potresti voler resettare seller e saleDate.
            if (!productDto.getSold() && existingProduct.getSeller() != null) {
                existingProduct.setSeller(null);
                existingProduct.setSaleDate(null);
            }
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductResponseDTO.class);
    }

    /**
     * Elimina un singolo prodotto dato il suo ID.
     * @param productId L'ID del prodotto da eliminare.
     */
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Prodotto con ID " + productId + " non trovato per l'eliminazione.");
        }
        productRepository.deleteById(productId);
    }

    /**
     * Elimina più prodotti dati i loro ID.
     * @param productIds La lista degli ID dei prodotti da eliminare.
     */
    @Transactional
    public void deleteProductsByIds(List<Long> productIds) {
        productRepository.deleteAllById(productIds);
    }

    /**
     * Recupera la cronologia dei prezzi per un prodotto specifico.
     * @param productId L'ID del prodotto.
     * @return Una lista di ProductPriceResponseDTO.
     * @throws IllegalArgumentException se il prodotto non viene trovato.
     */
    public List<ProductPriceResponseDTO> getProductPriceHistory(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto con ID " + productId + " non trovato per recuperare la cronologia prezzi."));

        return product.getPriceHistory().stream()
                .map(priceRecord -> modelMapper.map(priceRecord, ProductPriceResponseDTO.class))
                .collect(Collectors.toList());
    }
}
