package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private FilmHandler filmHandler = new FilmHandler();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Начато создание фильма. Получен объект {}", film);
        return filmHandler.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Начато обновление фильма. Получен объект {}", film);
        return filmHandler.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        log.debug("Начат возврат всех фильмов");
        return filmHandler.getAll();
    }

    //Для целей тестирования
    public FilmHandler getFilmHandler() {
        log.trace("Запрошен доступ для filmHandler для целей Тестирования");
        return filmHandler;
    }
}
