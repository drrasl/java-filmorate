package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final UserStorage inMemoryUserStorage;

    private final Map<Long, Film> filmStorage = new HashMap<>();
    private final Map<Long, Set<Long>> likeStorage = new HashMap<>(); // <Id фильма, Сет из айди пользователей>

    private long generatedId = 0;

    @Override
    public Film create(Film film) {
        film.setId(++generatedId);
        filmStorage.put(film.getId(), film);
        log.debug("Фильм добавлен в хранилище");
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!filmStorage.containsKey(film.getId()) | film.getId() == null) {
            log.debug("Запрашиваемый при обновлении фильм не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый фильм: " + film + " не найден");
        }
        filmStorage.put(film.getId(), film);
        log.debug("Фильм найден и обновлен в хранилище");
        return film;
    }

    @Override
    public Film delete(Long filmId) {
        if (!filmStorage.containsKey(filmId)) {
            log.debug("Запрашиваемый на удаление фильм не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый фильм с ID: " + filmId + " не найден");
        }
        Film filmToDelete = filmStorage.get(filmId);
        filmStorage.remove(filmId);
        log.debug("Фильм найден и удален из хранилища");
        return filmToDelete;
    }

    @Override
    public List<Film> getAll() {
        log.debug("Возвращаем все фильмы из хранилища");
        return new ArrayList<>(filmStorage.values());
    }

    //Метод для целей тестирования
    @Override
    public void clear() {
        log.trace("Очищаем хранилище фильмов для целей Тестирования");
        filmStorage.clear();
        generatedId = 0;
    }

    //Ниже приведена логика работы с фильмами и лайками.
    //Предлагается всегда возвращать айди пользователя, кто поставил лайк. Тогда не будет путаницы, что за айди вернулся.

    @Override
    public Long addLikeToFilm(Long filmId, Long userId) {
        validationOfFilmAndUser(filmId, userId);
        log.trace("Добавляем фильму с Id {} в список лайков пользователя с Id {}", filmId, userId);
        if (!likeStorage.containsKey(filmId)) {
            log.debug("Фильма с Id {} еще нет в базе лайков: создадим список пользователей, кто поставил лайк", filmId);
            log.trace("Создаем Set<Long> и добавим туда пользователя с Id {} ", userId);
            Set<Long> usersWhoPutLike = new HashSet<>();
            usersWhoPutLike.add(userId);
            log.trace("Добавляем фильм c Id {} в хранилище лайков", filmId);
            likeStorage.put(filmId, usersWhoPutLike);
        } else {
            log.debug("Фильм с Id {} есть в базе лайков: выберем его и добавим ему пользователя, который поставил" +
                    "лайк", filmId);
            Set<Long> usersWhoPutLike = likeStorage.get(filmId);
            if (!usersWhoPutLike.contains(userId)) {
                log.trace("Добавили фильму с Id {} пользователя с Id {}, который поставил лайк ", filmId, userId);
                usersWhoPutLike.add(userId);
            } else {
                throw new DuplicatedDataException("Пользователь с Id: " + userId + " уже ставил лайк фильму" +
                        " с Id: " + filmId);
            }
        }
        return userId;
    }

    @Override
    public Long removeLikeFromFilm(Long filmId, Long userId) {
        validationOfFilmAndUser(filmId, userId);
        log.trace("Удаляем фильму с Id {} лайк из списка лайков от пользователя с Id {}", filmId, userId);
        if (!likeStorage.containsKey(filmId)) {
            log.debug("У фильма с Id {} еще нет лайков, удаление лайка не возможно", filmId);
            throw new DataNotFoundException("У фильма с  Id " + filmId + " нет лайков, а значит удаление " +
                    "пользователя с Id " + userId + ", который желает удалить лайк невозможно");
        } else {
            log.debug("Фильм с Id {} есть в базе лайков: выберем его и удалим ему пользователя, который поставил" +
                    "лайк", filmId);
            Set<Long> usersWhoPutLike = likeStorage.get(filmId);
            if (usersWhoPutLike.contains(userId)) {
                log.trace("Удаляем у фильма с Id {} лайк от пользователя с Id {}", filmId, userId);
                usersWhoPutLike.remove(userId);
            } else {
                throw new DuplicatedDataException("Пользователь с Id: " + userId + " еще не ставил лайк фильму" +
                        " с Id: " + filmId + ", а значит удалять нечего");
            }
        }
        return userId;
    }

    @Override
    public List<Film> getListOfPopularFilms(Integer count) {
        log.debug("Возвращаем фильмы из хранилища в кол-ве count = {} и отсортированными по кол-ву лайков", count);
        return getAll().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(
                                (Film film) -> likeStorage.getOrDefault(film.getId(), Collections.emptySet()).size()
                        ).reversed()
                )
                .limit(count)
                .toList();
    }

    private void validationOfFilmAndUser(Long filmId, Long userId) {
        List<User> users = inMemoryUserStorage.getAll();
        User userWithRequestedId = users.stream()
                .filter(Objects::nonNull)
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Пользователь с Id: " + userId + " не найден"));
        log.trace("Пользователь с Id {} есть в списке пользователей", userWithRequestedId);
        if (!filmStorage.containsKey(filmId)) {
            log.debug("Фильма с указанным Id {} не найдено", filmId);
            throw new DataNotFoundException("Фильм с Id: " + filmId + " не найден");
        }
    }
}
