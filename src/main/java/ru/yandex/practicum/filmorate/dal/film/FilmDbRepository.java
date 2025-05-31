package ru.yandex.practicum.filmorate.dal.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenreCollection;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class FilmDbRepository implements FilmStorage {

    protected final JdbcTemplate jdbc;
    protected final RowMapper<Film> mapper;
    protected final RowMapper<Genres> genreRowMapper;
    protected final RowMapper<Mpa> mpaRowMapper;
    protected final RowMapper<FilmGenreCollection> filmGenreCollectionRowMapper;

    @Autowired
    public FilmDbRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, RowMapper<Genres> genreRowMapper, RowMapper<Mpa> mpaRowMapper,
                            RowMapper<FilmGenreCollection> filmGenreCollectionRowMapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
        this.filmGenreCollectionRowMapper = filmGenreCollectionRowMapper;
    }

    private static final String INSERT_QUERY = "INSERT INTO filmStorage(name, description, releaseDate, duration, rating_mpa_ID) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE filmStorage SET name = ?, description = ?, releaseDate = ?, " +
            "duration = ?, rating_mpa_ID = ?  WHERE film_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM filmStorage WHERE film_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM filmStorage";
    private static final String DELETE_ALL_QUERY = "DELETE FROM filmStorage";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM filmStorage WHERE film_ID = ?";
    private static final String SELECT_MAX_LIKES_FILMS_IN_QTY_OF = "SELECT fs.*, COUNT(ls.user_ID) AS likes_count " +
            "FROM filmStorage AS fs LEFT JOIN likeStorage AS ls ON fs.film_ID = ls.film_ID GROUP BY fs.film_ID " +
            "ORDER BY likes_count DESC LIMIT ?";
    private static final String INSERT_LIKE = "INSERT INTO likeStorage (film_ID, user_ID) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likeStorage WHERE film_ID = ? AND user_ID = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO filmGenres (film_ID, genre_ID) VALUES (?, ?)";
    private static final String GET_GENRES_BY_FILM_ID = "SELECT g.genre_ID, g.genre_name FROM filmGenres AS fg " +
            "JOIN genres g ON fg.genre_ID = g.genre_ID WHERE fg.film_ID = ?";
    private static final String DELETE_FILM_GENRES = "DELETE FROM filmGenres WHERE film_ID = ?";
    private static final String GET_MPA_NAME_BY_FILM_ID = "SELECT r.* FROM ratingOfFilmByMpa AS r JOIN " +
            "filmStorage AS f ON r.rating_mpa_ID = f.rating_mpa_ID WHERE f.film_ID = ?";
    private static final String FIND_ALL_FILM_GENRE_COLLECTION = "SELECT * FROM filmGenres";
    private static final String FIND_ALL_MPA = "SELECT * FROM ratingOfFilmByMpa";
    private static final String FIND_ALL_GENRES = "SELECT * FROM genres";

    @Transactional
    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(film.getReleaseDate().atStartOfDay()));
            ps.setLong(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new DataNotFoundException("Не удалось получить ID созданного фильма");
        }
        film.setId(id);
        addGenresToFilmDataBase(film);
        log.debug("Фильм добавлен в базу данных. Id = {}", id);
        return film;
    }

    @Transactional
    @Override
    public Film update(Film film) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY, film.getName(), film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()), film.getDuration(), film.getMpa().getId(),
                film.getId());
        if (rowsUpdated == 0) {
            throw new DataNotFoundException("Не удалось обновить данные, фильм с id = " + film.getId() + " не найден");
        }
        addGenresToFilmDataBase(film);
        log.debug("Фильм найден и обновлен в хранилище");
        return film;
    }

    @Override
    public Film delete(Long filmId) {
        Film film = getFilmById(filmId);
        int rowsUpdated = jdbc.update(DELETE_QUERY, filmId);
        if (rowsUpdated == 0) {
            throw new DataNotFoundException("Не удалось удалить фильм с id = " + filmId);
        }
        log.debug("Фильм найден и удален из хранилища");
        return film;
    }

    @Override
    public List<Film> getAll() {
        log.debug("Возвращаем все фильмы из хранилища");
        List<Film> films = jdbc.query(FIND_ALL_QUERY, mapper);
        List<Genres> genres = jdbc.query(FIND_ALL_GENRES, genreRowMapper);
        List<FilmGenreCollection> filmGenreCollections = jdbc.query(FIND_ALL_FILM_GENRE_COLLECTION, filmGenreCollectionRowMapper);
        List<Mpa> mpas = jdbc.query(FIND_ALL_MPA, mpaRowMapper);

        List<Film> completeFilms = new ArrayList<>();

        for (Film film : films) {
            List<Integer> idOfGenresOfEachFilm = new ArrayList<>();
            List<Genres> genresOfEachFilm = new ArrayList<>();
            Long id = film.getId();
            List<FilmGenreCollection> genresById = filmGenreCollections.stream()
                    .filter(item -> id == item.getFilmId()).toList();
            for (FilmGenreCollection item : genresById) {
                idOfGenresOfEachFilm.add(item.getGenreId());
            }
            genresOfEachFilm = idOfGenresOfEachFilm.stream().map(genreId -> new Genres(genreId,
                    genres.stream().filter(item -> item.getId() == genreId)
                            .map(Genres::getName).findFirst().orElse(null)
            )).toList();
            film.setGenres(genresOfEachFilm);
            Mpa mpaOfFilm = new Mpa();
            mpaOfFilm.setId(film.getMpa().getId());
            mpaOfFilm.setName(mpas.stream().filter(mpa -> mpa.getId() == film.getMpa().getId())
                    .findFirst()
                    .map(mpa -> mpa.getName())
                    .orElse(null));
            completeFilms.add(film);
        }
        return completeFilms;
    }

    //Метод очистки хранилища для целей тестирования
    @Override
    public void clear() {
        log.trace("Очищаем хранилище фильмов для целей Тестирования");
        int rowsUpdated = jdbc.update(DELETE_ALL_QUERY);
        log.debug("Удалено {} записей", rowsUpdated);
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (filmId == null) {
            throw new IllegalArgumentException("ID фильма не может быть null");
        }
        try {
            log.debug("Возвращаем фильм с id = {} из хранилища", filmId);
            return getCompleteFilm(filmId);
        } catch (EmptyResultDataAccessException ex) {
            log.debug("Фильм не найден - вернем null в сервис");
            return null;
        }
    }

    private void addGenresToFilmDataBase(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        List<Genres> uniqueGenres = film.getGenres().stream()
                .distinct()
                .toList();
        //Удаляем старые жанры (для случая update)
        jdbc.update(DELETE_FILM_GENRES, film.getId());

        for (Genres genre : uniqueGenres) {
            jdbc.update(INSERT_FILM_GENRE, film.getId(), genre.getId());
        }
    }

    private Film getCompleteFilm(Long filmId) {
        Film film = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, filmId);
        if (film == null) {
            return null;
        }
        Mpa mpa = jdbc.queryForObject(GET_MPA_NAME_BY_FILM_ID, mpaRowMapper, filmId);
        film.setMpa(mpa);

        List<Genres> genres = jdbc.query(GET_GENRES_BY_FILM_ID, genreRowMapper, filmId);
        film.setGenres(genres);

        return film;
    }

    //Ниже приведена логика работы с фильмами и лайками.
    //Предлагается всегда возвращать айди пользователя, кто поставил лайк. Тогда не будет путаницы, что за айди вернулся.
    @Override
    public Long addLikeToFilm(Long filmId, Long userId) {
        jdbc.update(INSERT_LIKE, filmId, userId);
        return userId;
    }

    @Override
    public Long removeLikeFromFilm(Long filmId, Long userId) {
        jdbc.update(REMOVE_LIKE, filmId, userId);
        return userId;
    }

    @Override
    public List<Film> getListOfPopularFilms(Integer count) {
        log.debug("Возвращаем {} фильмов из хранилища, отсортированных по максимуму лайков", count);
        return jdbc.query(SELECT_MAX_LIKES_FILMS_IN_QTY_OF, mapper, count);
    }

    //Метод только для целей тестирования
    public int countLikesInLikeStorage(Long filmId) {
        return jdbc.queryForObject("SELECT COUNT(user_ID) FROM likeStorage WHERE film_ID = ?", Integer.class, filmId);
    }
}
