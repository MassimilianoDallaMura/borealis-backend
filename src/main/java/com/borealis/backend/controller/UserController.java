package com.borealis.backend.controller;

import com.borealis.backend.dto.request.UserRequestDTO; // UserRequestDTO ora è solo per aggiornare nome/email
import com.borealis.backend.dto.response.UserResponseDTO;
import com.borealis.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importa PreAuthorize per la sicurezza degli endpoint
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://bvm.kaninchenhaus.org")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- RIMOSSO IL METODO `POST /api/users` PER LA CREAZIONE UTENTE ---
    // La creazione di nuovi utenti (anche con ruoli base) è ora responsabilità esclusiva di AuthController.
    // L'endpoint per creare utenti è ora in AuthController: /api/auth/create-user (per admin)
    // e /api/auth/register (per registrazione pubblica, se mantenuta).

    /**
     * GET /api/users/{id}
     * Recupera un utente tramite ID.
     * Richiede autenticazione. Accessibile a ROLE_USER e ROLE_SUPERUSER.
     *
     * @param id L'ID dell'utente.
     * @return ResponseEntity con il UserResponseDTO trovato e stato HTTP 200 OK, o 404 Not Found.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'SUPERUSER')") // Permetti a USER e SUPERUSER di vedere un utente
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET /api/users
     * Recupera tutti gli utenti.
     * Richiede il ruolo 'SUPERUSER'.
     *
     * @return ResponseEntity con una lista di UserResponseDTO e stato HTTP 200 OK.
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')") // Solo i SUPERUSER possono recuperare tutti gli utenti
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * PUT /api/users/{id}
     * Aggiorna un utente esistente (solo nome ed email).
     * Accessibile a 'SUPERUSER' o all'utente stesso che aggiorna il proprio profilo.
     *
     * @param id L'ID dell'utente da aggiornare.
     * @param userDto L'oggetto UserRequestDTO con i dati aggiornati (solo nome ed email).
     * @return ResponseEntity con l'UserResponseDTO aggiornato e stato HTTP 200 OK, o 404 Not Found.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERUSER') or #id == authentication.principal.id") // Un SUPERUSER o l'utente stesso può aggiornare nome/email
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userDto) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userDto);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /api/users/{id}/password
     * Aggiorna la password di un utente.
     * Accessibile a 'SUPERUSER' o all'utente stesso che aggiorna la propria password.
     *
     * @param id L'ID dell'utente.
     * @param request DTO con la nuova password.
     * @return ResponseEntity con l'UserResponseDTO aggiornato e stato HTTP 200 OK, o 404 Not Found.
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('SUPERUSER') or #id == authentication.principal.id") // Un SUPERUSER o l'utente stesso può aggiornare la password
    public ResponseEntity<UserResponseDTO> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UserService.UpdatePasswordRequestDTO request) { // Usa il DTO annidato di UserService
        try {
            UserResponseDTO updatedUser = userService.updatePassword(id, request);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /api/users/{id}/roles
     * Aggiorna i ruoli di un utente.
     * Accessibile SOLO a 'SUPERUSER'.
     *
     * @param id L'ID dell'utente.
     * @param request DTO con i nuovi ruoli.
     * @return ResponseEntity con l'UserResponseDTO aggiornato e stato HTTP 200 OK, o 404 Not Found.
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('SUPERUSER')") // Solo i SUPERUSER possono modificare i ruoli
    public ResponseEntity<UserResponseDTO> updateRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserService.UpdateRolesRequestDTO request) { // Usa il DTO annidato di UserService
        try {
            UserResponseDTO updatedUser = userService.updateRoles(id, request);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE /api/users/{id}
     * Elimina un utente.
     * Richiede il ruolo 'SUPERUSER'.
     *
     * @param id L'ID dell'utente da eliminare.
     * @return ResponseEntity con stato HTTP 204 No Content se eliminato, o 404 Not Found.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERUSER')") // Solo i SUPERUSER possono eliminare utenti
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}