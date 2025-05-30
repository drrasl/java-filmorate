package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.service.film.GenreService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genres> getAllGenres() {
        log.debug("Начат возврат всех жанров");
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genres getGenreById(@NotNull @Positive @PathVariable("id") Integer genreId) {
        log.debug("Начат возврат жанра по его id");
        return genreService.getGenreById(genreId);
    }
}
