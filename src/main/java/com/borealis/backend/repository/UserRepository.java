package com.borealis.backend.repository;

import com.borealis.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Non strettamente necessario con Spring Data JPA ma buona pratica
public interface UserRepository extends JpaRepository<User, Long> {
    // Trova un utente per email. Cruciale per UserDetailsServiceImpl.
    Optional<User> findByEmail(String email);

    // Utile per verificare se un'email esiste già (es. durante la registrazione)
    Boolean existsByEmail(String email);
}