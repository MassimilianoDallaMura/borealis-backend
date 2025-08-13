package com.borealis.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    // Costruttore che accetta un messaggio per l'eccezione
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Puoi aggiungere costruttori aggiuntivi se necessario
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}