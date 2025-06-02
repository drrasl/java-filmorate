package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class GenreDbRepository implements GenreStorage {

    protected final JdbcTemplate jdbc;
    protected final RowMapper<Genres> genreRowMapper;

    private static final String SELECT_ALL_GENRES = "SELECT * FROM genres";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_ID = ?";


    @Override
    public List<Genres> getAllGenres() {
        log.debug("Возвращаем все жанры из хранилища");
        return jdbc.query(SELECT_ALL_GENRES, genreRowMapper);
    }

    @Override
    public Optional<Genres> getGenreById(Integer genreId) {
        return jdbc.query(GET_GENRE_BY_ID, genreRowMapper, genreId)
                .stream()
                .findFirst();
    }
}
