package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.film.mapper.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.dal.film.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.film.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.film.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.user.UserDbRepository;
import ru.yandex.practicum.filmorate.dal.user.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbRepository.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class, FilmGenreRowMapper.class, UserDbRepository.class, UserRowMapper.class})
class FilmDbRepositoryTest {
    private final FilmDbRepository filmDbRepository;
    private final UserDbRepository userDbRepository;

    @AfterEach
    void clear() {
        log.trace("Очистка хранилища фильмов после теста");
        filmDbRepository.clear();
        userDbRepository.clear();
    }

    @Test
    public void createFilmTest() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2020, 8, 15))
                .duration(120L)
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genres(1, "Комедия"), new Genres(2, "Драма")))
                .build();
        filmDbRepository.create(film);

        Film film1 = filmDbRepository.getFilmById(1L);
        assertEquals(film, film1, "Фильмы не совпадают");
    }

    @Test
    public void updateFilmTest() {
        Film film = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2021, 8, 15))
                .duration(150L)
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genres(1, "Комедия"), new Genres(2, "Драма")))
                .build();
        filmDbRepository.create(film);

        Film film1 = filmDbRepository.getFilmById(1L);
        assertEquals(film, film1, "Фильмы не совпадают");
    }

    @Test
    public void deleteFilmTest() {
        Film film = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2021, 8, 15))
                .duration(150L)
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genres(1, "Комедия"), new Genres(2, "Драма")))
                .build();
        filmDbRepository.create(film);
        Film film1 = filmDbRepository.delete(1L);
        assertNull(filmDbRepository.getFilmById(1L), "Объект не удалился из базы");
        assertEquals(film, film1, "Фильмы не совпадают");
    }

    @Test
    public void addLikeToFilmTest() {
        ru.yandex.practicum.filmorate.model.User user = User.builder()
                .email("email@email.com")
                .login("emailman")
                .name("Vasily")
                .birthday(LocalDate.of(2000, 8, 22))
                .build();
        userDbRepository.create(user);
        ru.yandex.practicum.filmorate.model.User user2 = User.builder()
                .email("gemail@gemail.com")
                .login("gemailman")
                .name("gVasily")
                .birthday(LocalDate.of(2001, 8, 22))
                .build();
        userDbRepository.create(user2);
        Film film = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2021, 8, 15))
                .duration(150L)
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genres(1, "Комедия"), new Genres(2, "Драма")))
                .build();
        filmDbRepository.create(film);
        Long userId = filmDbRepository.addLikeToFilm(1L, 1L);
        Long userId2 = filmDbRepository.addLikeToFilm(1L, 2L);

        Integer qty = filmDbRepository.countLikesInLikeStorage(1L);
        assertEquals(2, qty, "Кол-во лайков не совпадает");
    }

    @Test
    public void deleteLikeToFilmTest() {
        ru.yandex.practicum.filmorate.model.User user = User.builder()
                .email("email@email.com")
                .login("emailman")
                .name("Vasily")
                .birthday(LocalDate.of(2000, 8, 22))
                .build();
        userDbRepository.create(user);
        ru.yandex.practicum.filmorate.model.User user2 = User.builder()
                .email("gemail@gemail.com")
                .login("gemailman")
                .name("gVasily")
                .birthday(LocalDate.of(2001, 8, 22))
                .build();
        userDbRepository.create(user2);
        Film film = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(2021, 8, 15))
                .duration(150L)
                .mpa(new Mpa(1, "G"))
                .genres(List.of(new Genres(1, "Комедия"), new Genres(2, "Драма")))
                .build();
        filmDbRepository.create(film);
        Long userId = filmDbRepository.addLikeToFilm(1L, 1L);
        Long userId2 = filmDbRepository.addLikeToFilm(1L, 2L);
        filmDbRepository.removeLikeFromFilm(1L, 1L);

        Integer qty = filmDbRepository.countLikesInLikeStorage(1L);
        assertEquals(1, qty, "Кол-во лайков не совпадает");
    }


}