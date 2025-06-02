package ru.yandex.practicum.filmorate.dal.film.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenreCollection;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenreRowMapper implements RowMapper<FilmGenreCollection> {
    @Override
    public FilmGenreCollection mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmGenreCollection(rs.getLong("film_ID"), rs.getInt("genre_ID"));
    }
}
