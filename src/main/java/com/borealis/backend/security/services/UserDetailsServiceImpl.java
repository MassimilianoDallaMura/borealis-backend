package com.borealis.backend.security.services;

import com.borealis.backend.model.User; // La tua entità User
import com.borealis.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository; // Inietta il tuo UserRepository

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security userà questo metodo per caricare i dettagli dell'utente.
        // Assumiamo che il "username" per il login sia l'email dell'utente.
        User user = userRepository.findByEmail(email) // Avrai bisogno di un metodo findByEmail nel tuo UserRepository
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

        return UserDetailsImpl.build(user); // Costruisce UserDetailsImpl dalla tua entità User
    }
}

