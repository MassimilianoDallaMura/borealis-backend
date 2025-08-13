package com.borealis.backend.service;

import com.borealis.backend.dto.request.LoginRequestDTO;
import com.borealis.backend.dto.request.RegisterRequestDTO;
import com.borealis.backend.dto.request.UserCreationRequestDTO;
import com.borealis.backend.dto.response.UserResponseDTO;
import com.borealis.backend.Enum.Role; // <-- Modificato: Usa il tuo Enum Role
import com.borealis.backend.model.User;
import com.borealis.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public UserResponseDTO createUser(UserCreationRequestDTO creationRequest) {
        if (userRepository.existsByEmail(creationRequest.getEmail())) {
            throw new IllegalArgumentException("Errore: L'email è già in uso!");
        }

        User user = new User();
        user.setName(creationRequest.getName());
        user.setEmail(creationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(creationRequest.getPassword()));

        Set<String> strRoles = creationRequest.getRoles();
        Set<String> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            roles.add(Role.ROLE_USER.name()); // <-- Modificato: Usa Role
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "SUPERUSER":
                    case "ROLE_SUPERUSER":
                        roles.add(Role.ROLE_SUPERUSER.name()); // <-- Modificato: Usa Role
                        break;
                    case "USER":
                    case "ROLE_USER":
                        roles.add(Role.ROLE_USER.name()); // <-- Modificato: Usa Role
                        break;
                    default:
                        throw new IllegalArgumentException("Errore: Ruolo non valido: " + role);
                }
            });
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO registerNewUser(RegisterRequestDTO registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Errore: L'email è già in uso!");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set<String> roles = new HashSet<>();
        roles.add(Role.ROLE_USER.name()); // <-- Modificato: Usa Role
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}