package com.borealis.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet; // Importa HashSet
import java.util.List;
import java.util.Set; // Importa Set

@Entity
@Table(name = "users") // La tabella nel database si chiamerà 'users'
@Data
@NoArgsConstructor // Lombok: genera costruttore senza argomenti
@AllArgsConstructor // Lombok: genera costruttore con tutti gli argomenti
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false) // L'email deve essere unica e non nulla
    private String email;

    @Column(name = "password", nullable = false) // La password non deve essere nulla
    private String password; // Password codificata (non in chiaro!)

    // Gestione dei ruoli: useremo un Set di stringhe per i nomi dei ruoli
    // @ElementCollection crea una tabella separata per i ruoli di ogni utente
    @ElementCollection(fetch = FetchType.EAGER) // Carica i ruoli insieme all'utente
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id")) // Tabella di join
    @Column(name = "role_name") // Colonna per il nome del ruolo nella tabella user_roles
    private Set<String> roles = new HashSet<>(); // Inizializza con un HashSet vuoto

    // Prodotti di proprietà di questo utente (quelli che ha inserito)
    // 'mappedBy' indica il nome del campo nell'entità Product che si riferisce a questo User come 'owner'
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("user-owned-products") // Nome per gestire la serializzazione JSON
    private List<Product> ownedProducts = new ArrayList<>();

    // Prodotti che questo utente ha venduto
    // 'mappedBy' indica il nome del campo nell'entità Product che si riferisce a questo User come 'seller'
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    @JsonManagedReference("user-sold-products") // Nome per gestire la serializzazione JSON
    private List<Product> soldProducts = new ArrayList<>();


    // Costruttore per la creazione di un nuovo utente (utile per la registrazione)
    // Ho rimosso @AllArgsConstructor di Lombok per definire un costruttore specifico per la creazione
    // che non includa le liste di prodotti che sono gestite dalle relazioni JPA.
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>(); // Inizializza i ruoli anche qui
    }

    // Metodo helper per aggiungere un ruolo
    public void addRole(String role) {
        this.roles.add(role);
    }
}