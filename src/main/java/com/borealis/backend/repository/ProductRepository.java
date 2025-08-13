package com.borealis.backend.repository;

import com.borealis.backend.model.Category;
import com.borealis.backend.model.User;
import com.borealis.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Aggiungi questa import
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByOwner(User user);
    List<Product> findByCategory(Category category);
    List<Product> findByOwnerId(Long ownerId);
}