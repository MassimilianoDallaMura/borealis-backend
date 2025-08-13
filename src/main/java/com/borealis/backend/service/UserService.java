package com.borealis.backend.service;

import com.borealis.backend.dto.request.UserRequestDTO;
import com.borealis.backend.dto.response.UserResponseDTO;
import com.borealis.backend.Enum.Role; // <-- Modificato: Usa il tuo Enum Role
import com.borealis.backend.model.User;
import com.borealis.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper, AuthService authService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.authService = authService;
    }


    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente con ID " + id + " non trovato."));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente con ID " + id + " non trovato per l'aggiornamento."));

        if (!existingUser.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Errore: La nuova email è già in uso!");
        }

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());

        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utente con ID " + id + " non trovato per l'eliminazione.");
        }
        userRepository.deleteById(id);
    }

    @Data
    public static class UpdatePasswordRequestDTO {
        @NotBlank(message = "La nuova password è obbligatoria")
        @Size(min = 6, max = 40, message = "La password deve avere tra 6 e 40 caratteri")
        private String newPassword;
    }

    @Transactional
    public UserResponseDTO updatePassword(Long id, UpdatePasswordRequestDTO request) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + id));

        userToUpdate.setPassword(authService.encodePassword(request.getNewPassword()));
        User updatedUser = userRepository.save(userToUpdate);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    @Data
    public static class UpdateRolesRequestDTO {
        private Set<String> roles;
    }

    @Transactional
    public UserResponseDTO updateRoles(Long id, UpdateRolesRequestDTO request) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + id));

        Set<String> newRoles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            request.getRoles().forEach(role -> {
                switch (role.toUpperCase()) {
                    case "SUPERUSER":
                    case "ROLE_SUPERUSER":
                        newRoles.add(Role.ROLE_SUPERUSER.name()); // <-- Modificato: Usa Role
                        break;
                    case "USER":
                    case "ROLE_USER":
                        newRoles.add(Role.ROLE_USER.name()); // <-- Modificato: Usa Role
                        break;
                    default:
                        throw new IllegalArgumentException("Errore: Ruolo non valido per aggiornamento: " + role);
                }
            });
        } else {
            newRoles.add(Role.ROLE_USER.name()); // <-- Modificato: Usa Role
        }
        userToUpdate.setRoles(newRoles);

        User updatedUser = userRepository.save(userToUpdate);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }
}