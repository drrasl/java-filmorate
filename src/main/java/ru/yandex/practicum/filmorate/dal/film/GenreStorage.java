package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    public List<Genres> getAllGenres();

    public Optional<Genres> getGenreById(Integer genreId);
}
