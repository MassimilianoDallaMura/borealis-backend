package com.borealis.backend.config;

import com.borealis.backend.Enum.Role;
import com.borealis.backend.model.User;
import com.borealis.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Creare un superutente se non esiste già
        if (userRepository.findByEmail("user@borealis.com").isEmpty()) {
            User superuser = new User();
            superuser.setName("User Borealis");
            superuser.setEmail("user@borealis.com");
            superuser.setPassword(passwordEncoder.encode("CaneMatto!")); // !!! CAMBIA QUESTA PASSWORD IN PRODUZIONE !!!
            Set<String> roles = new HashSet<>();
            roles.add(Role.ROLE_SUPERUSER.name());
            roles.add(Role.ROLE_USER.name()); // Un superuser ha anche i privilegi di un user normale
            superuser.setRoles(roles);
            userRepository.save(superuser);
            System.out.println("Superuser 'user@borealis.com' creato con successo!");
        }

        // Puoi aggiungere qui altri dati di inizializzazione se necessario
    }
}