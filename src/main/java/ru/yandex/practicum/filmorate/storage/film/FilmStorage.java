package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Film delete(Long filmId);

    List<Film> getAll();

    void clear();

    Long addLikeToFilm(Long filmId, Long userId);

    Long removeLikeFromFilm(Long filmId, Long userId);

    List<Film> getListOfPopularFilms(Integer count);
}
