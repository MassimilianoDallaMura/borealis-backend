package com.borealis.backend.repository;

import com.borealis.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Puoi aggiungere metodi personalizzati qui se necessari, es:
    Optional<Category> findByName(String name);
}