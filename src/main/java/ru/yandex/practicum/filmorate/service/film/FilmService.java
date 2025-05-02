package ru.yandex.practicum.filmorate.service.film;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    public Film create(Film film) {
        if (isItAfterCinemaBirthday(film)) {
            log.trace("Фильм отправлен в хранилище");
            return inMemoryFilmStorage.create(film);
        }
        log.trace("Фильм не создан");
        return null;
    }

    public Film update(Film film) {
        if (isItAfterCinemaBirthday(film)) {
            log.trace("Фильм отправлен на обновление в хранилище");
            return inMemoryFilmStorage.update(film);
        }
        log.trace("Фильм не обновлен");
        return null;
    }

    public Film delete(Long filmId) {
        log.trace("Фильм отправлен на удаление в хранилище");
        return inMemoryFilmStorage.delete(filmId);
    }

    public List<Film> getAll() {
        log.trace("Отправляем запрос на возврат всеx фильмов из хранилища");
        return inMemoryFilmStorage.getAll();
    }

    private boolean isItAfterCinemaBirthday(Film film) {
        log.trace("Начинаем проверку на дату релиза фильма");
        if (!film.getReleaseDate().isAfter(cinemaBirthday)) {
            log.trace("Введена дата релиза меньше, чем 28.12.1895");
            throw new ValidationException("Дата Фильма не может быть меньше 28.12.1895");
        }
        return true;
    }

    //Метод очистки хранилища для целей тестирования
    public void clear() {
        log.trace("Запрос отправлен в хранилище для очистки хранилища фильмов для целей Тестирования");
        inMemoryFilmStorage.clear();
    }

    //Ниже приведена логика работы с фильмами и лайками.
    //Предлагается всегда возвращать айди пользователя, кто поставил лайк. Тогда не будет путаницы, что за айди вернулся.

    public Long addLikeToFilm(Long filmId, Long userId) {
        log.trace("Отправляем лайк для фильма в хранилище");
        return inMemoryFilmStorage.addLikeToFilm(filmId, userId);
    }

    public Long removeLikeFromFilm(Long filmId, Long userId) {
        log.trace("Отправляем лайк для его удаления из хранилища");
        return inMemoryFilmStorage.removeLikeFromFilm(filmId, userId);
    }

    public List<Film> getListOfPopularFilms(Integer count) {
        log.trace("Отправляем запрос на возврат count {} фильмов из хранилища, отсортированных по кол-ву лайков", count);
        return inMemoryFilmStorage.getListOfPopularFilms(count);
    }
}
