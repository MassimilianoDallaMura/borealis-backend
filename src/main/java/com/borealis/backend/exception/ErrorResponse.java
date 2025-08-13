package com.borealis.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
private LocalDateTime timestamp;
private int status;
private String error; // Tipo di errore HTTP (es. "Bad Request")
private String message; // Messaggio specifico
private String path;
private List<String> details; // Per errori di validazione o altri dettagli
}
