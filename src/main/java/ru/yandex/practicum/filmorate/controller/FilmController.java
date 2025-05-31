package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public FilmDto create(@Valid @RequestBody Film film) {
        log.debug("Начато создание фильма. Получен объект {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody Film film) {
        log.debug("Начато обновление фильма. Получен объект {}", film);
        return filmService.update(film);
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable @Positive Long filmId) {
        log.debug("Начат возврат фильма с filmId = {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @DeleteMapping("/{filmId}")
    public FilmDto delete(@PathVariable @Positive Long filmId) {
        log.debug("Начато удаление фильма с filmId = {}", filmId);
        return filmService.delete(filmId);
    }

    @GetMapping
    public List<FilmDto> getAll() {
        log.debug("Начат возврат всех фильмов");
        return filmService.getAll();
    }

    //Возврат объекта filmService для целей тестирования
    public FilmService getFilmService() {
        log.trace("Запрошен доступ для filmService для целей Тестирования");
        return filmService;
    }

    //Ниже приведена логика работы с фильмами и лайками.
    //Предлагается всегда возвращать айди пользователя, кто поставил лайк. Тогда не будет путаницы, что за айди вернулся.

    @PutMapping("/{id}/like/{userId}")
    public Long addLikeToFilm(@NotNull @Positive @PathVariable("id") Long filmId,
                              @NotNull @Positive @PathVariable Long userId) {
        log.debug("Начато добавление лайка фильму с Id {} от пользователя с Id {}", filmId, userId);
        return filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Long removeLikeFromFilm(@NotNull @Positive @PathVariable("id") Long filmId,
                                   @NotNull @Positive @PathVariable Long userId) {
        log.debug("Начато удаление лайка фильму с Id {} от пользователя с Id {}", filmId, userId);
        return filmService.removeLikeFromFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getListOfPopularFilms(@Positive @RequestParam(defaultValue = "10") Integer count) {
        log.debug("Начат возврат фильмов, отсортированных по кол-ву лайков и в кол-ве count = {}", count);
        return filmService.getListOfPopularFilms(count);
    }
}
