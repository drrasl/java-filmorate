package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FilmHandler {

    private final Map<Long, Film> filmStorage = new HashMap<>();
    private long generatedId = 0;
    private final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    public Film create(@Valid Film film) {
        if (!isItAfterCinemaBirthday(film)) {
            log.trace("Введена дата релиза меньше, чем 28.12.1895");
            throw new ValidationException("Дата Фильма не может быть меньше 28.12.1895");
        }

        film.setId(++generatedId);
        filmStorage.put(film.getId(), film);
        log.debug("Фильм добавлен в хранилище");
        return film;
    }

    public Film update(@Valid Film film) {
        if (!isItAfterCinemaBirthday(film)) {
            log.trace("Введена дата релиза меньше, чем 28.12.1895");
            throw new ValidationException("Дата Фильма не может быть меньше 28.12.1895");
        }
        if (!filmStorage.containsKey(film.getId()) | film.getId() == null) {
            log.debug("Запрашиваемый при обновлении фильм не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый фильм: " + film + " не найден");
        }
        filmStorage.put(film.getId(), film);
        log.debug("Фильм найден и обновлен в хранилище");
        return film;
    }

    public List<Film> getAll() {
        log.debug("Возвращаем все фильмы из хранилища");
        return new ArrayList<>(filmStorage.values());
    }

    private boolean isItAfterCinemaBirthday(@Valid Film film) {
        log.trace("Начинаем проверку на дату релиза фильма");
        return film.getReleaseDate().isAfter(cinemaBirthday);
    }

    //Метод для целей тестирования
    protected void clear() {
        log.trace("Запрошен доступ для очистки хранилища фильмов для целей Тестирования");
        filmStorage.clear();
        generatedId = 0;
    }
}
