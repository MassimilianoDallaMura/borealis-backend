package com.borealis.backend.security.services;

import com.borealis.backend.model.User; // La tua entità User
import com.fasterxml.jackson.annotation.JsonIgnore; // Per ignorare la password nella serializzazione JSON
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Set; // Importa Set

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name; // Il nome completo dell'utente
    private String email; // L'email dell'utente, usata come username per Spring Security

    @JsonIgnore // Non serializzare la password nel JSON di risposta per sicurezza
    private String password;

    private Collection<? extends GrantedAuthority> authorities; // Ruoli/Autorizzazioni dell'utente

    public UserDetailsImpl(Long id, String name, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Metodo statico per costruire UserDetailsImpl dalla tua entità User
    public static UserDetailsImpl build(User user) {
        // Mappa i ruoli dell'utente (assumendo che user.getRoles() restituisca un Set<String> di nomi di ruolo come "ROLE_ADMIN")
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(roleName -> new SimpleGrantedAuthority(roleName)) // Mappa il nome del ruolo a SimpleGrantedAuthority
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    // AGGIUNTO: Getter per il campo 'name'
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Usiamo l'email come username per Spring Security
    }

    // Metodi che indicano lo stato dell'account (di solito true per default a meno di logica specifica)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Metodo equals per confrontare gli utenti (importante per Spring Security)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
