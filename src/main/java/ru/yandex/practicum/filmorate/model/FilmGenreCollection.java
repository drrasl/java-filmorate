package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class FilmGenreCollection {

    @NotNull
    Long filmId;

    @NotNull
    Integer genreId;
}
