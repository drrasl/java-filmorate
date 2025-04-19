package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidLoginException extends ResponseStatusException {
    public InvalidLoginException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
