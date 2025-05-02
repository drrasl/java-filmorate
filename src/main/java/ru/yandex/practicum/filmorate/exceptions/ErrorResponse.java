package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

@Getter
public class ErrorResponse {
    // название ошибки
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
