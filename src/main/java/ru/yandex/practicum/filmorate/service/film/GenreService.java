package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.GenreStorage;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genres> getAllGenres() {
        log.debug("Отправляем запрос на возврат всех жанров");
        return genreStorage.getAllGenres();
    }

    public Genres getGenreById(Integer genreId) {
        log.debug("Отправляем запрос на возврат жанра по его ID");
        return genreStorage.getGenreById(genreId)
                .orElseThrow(() -> new DataNotFoundException("Жанр с ID " + genreId + " не найден"));
    }
}
