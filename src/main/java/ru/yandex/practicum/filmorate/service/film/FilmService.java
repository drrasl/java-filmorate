package ru.yandex.practicum.filmorate.service.film;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmDbRepo;
    private final UserStorage userDbRepo;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    public FilmDto create(Film film) {
        if (isItAfterCinemaBirthday(film)) {
            validationOfMpaAndGenre(film);
            log.trace("Фильм отправлен в хранилище");
            return FilmMapper.mapToFilmDto(filmDbRepo.create(film));
        }
        log.trace("Фильм не создан");
        return null;
    }

    public FilmDto update(Film film) {
        if (isItAfterCinemaBirthday(film)) {
            validationOfMpaAndGenre(film);
            log.trace("Фильм отправлен на обновление в хранилище");
            return FilmMapper.mapToFilmDto(filmDbRepo.update(film));
        }
        log.trace("Фильм не обновлен");
        return null;
    }

    public FilmDto getFilmById(Long filmId) {
        log.debug("Отправим запрос на возврат фильма с filmId = {}", filmId);
        return FilmMapper.mapToFilmDto(filmDbRepo.getFilmById(filmId));
    }

    public FilmDto delete(Long filmId) {
        log.trace("Фильм отправлен на удаление в хранилище");
        return FilmMapper.mapToFilmDto(filmDbRepo.delete(filmId));
    }

    public List<FilmDto> getAll() {
        log.trace("Отправляем запрос на возврат всеx фильмов из хранилища");
        return filmDbRepo.getAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
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
        filmDbRepo.clear();
    }

    //Ниже приведена логика работы с фильмами и лайками.
    //Предлагается всегда возвращать айди пользователя, кто поставил лайк. Тогда не будет путаницы, что за айди вернулся.

    public Long addLikeToFilm(Long filmId, Long userId) {
        validationOfFilmAndUser(filmId, userId);
        log.trace("Отправляем лайк для фильма в хранилище");
        return filmDbRepo.addLikeToFilm(filmId, userId);
    }

    public Long removeLikeFromFilm(Long filmId, Long userId) {
        validationOfFilmAndUser(filmId, userId);
        log.trace("Отправляем лайк для его удаления из хранилища");
        return filmDbRepo.removeLikeFromFilm(filmId, userId);
    }

    public List<FilmDto> getListOfPopularFilms(Integer count) {
        log.trace("Отправляем запрос на возврат count {} фильмов из хранилища, отсортированных по кол-ву лайков", count);
        return filmDbRepo.getListOfPopularFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private void validationOfFilmAndUser(Long filmId, Long userId) {
        if (userDbRepo.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь с Id: " + userId + " не найден");
        }
        log.trace("Пользователь с Id {} есть в списке пользователей", userId);
        if (filmDbRepo.getFilmById(filmId) == null) {
            throw new DataNotFoundException("Фильм с Id: " + filmId + " не найден");
        }
        log.trace("Фильм с указанным Id {} есть в списке фильмов", filmId);
    }

    private void validationOfMpaAndGenre(Film film) {
        film.getGenres().stream()
                .map(genre -> genre.getId())
                .forEach(genreService::getGenreById);
        mpaService.getMpaById(film.getMpa().getId());
    }
}
