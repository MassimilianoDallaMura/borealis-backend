package com.borealis.backend.controller;

import com.borealis.backend.dto.request.LoginRequestDTO;
import com.borealis.backend.dto.request.RegisterRequestDTO;
import com.borealis.backend.dto.request.UserCreationRequestDTO; // <-- Importa il nuovo DTO
import com.borealis.backend.dto.response.JwtResponseDTO;
import com.borealis.backend.dto.response.UserResponseDTO;
import com.borealis.backend.security.jwt.JwtUtils;
import com.borealis.backend.security.services.UserDetailsImpl;
import com.borealis.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- Importa PreAuthorize
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "https://bvm.kaninchenhaus.org")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthService authService;

    /**
     * POST /api/auth/login
     * Endpoint per l'autenticazione dell'utente.
     * Riceve email e password, autentica l'utente e restituisce un JWT.
     *
     * @param loginRequest DTO contenente email e password.
     * @return ResponseEntity con JwtResponseDTO in caso di successo, o errore.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponseDTO(jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                roles));
    }

    /**
     * POST /api/auth/create-user
     * Endpoint per la creazione di un nuovo utente da parte di un SUPERUSER.
     * Questo endpoint richiede che l'utente autenticato abbia il ruolo 'SUPERUSER'.
     * Riceve nome, email, password e un set di ruoli per il nuovo utente.
     *
     * @param creationRequest DTO contenente nome, email, password e ruoli per la creazione.
     * @return ResponseEntity con il UserResponseDTO dell'utente creato o errore.
     */
    @PostMapping("/create-user") // NUOVO ENDPOINT: Rinomina questo per chiarezza se il tuo "/register" non è pubblico
    @PreAuthorize("hasRole('SUPERUSER')") // Solo i superutenti possono creare utenti
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreationRequestDTO creationRequest) {
        try {
            UserResponseDTO createdUser = authService.createUser(creationRequest); // Chiama il metodo appropriato nel service
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Se vuoi un endpoint di registrazione pubblica che assegni SEMPRE ROLE_USER,
    // puoi mantenere questo metodo (o rinominarlo)
    // Se non vuoi registrazione pubblica e solo l'admin crea utenti, rimuovi questo.
    @PostMapping("/register") // Questo endpoint potrebbe essere rinominato o rimosso se solo gli admin creano utenti
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            // Questo endpoint assegna sempre ROLE_USER
            UserResponseDTO registeredUser = authService.registerNewUser(registerRequest);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}