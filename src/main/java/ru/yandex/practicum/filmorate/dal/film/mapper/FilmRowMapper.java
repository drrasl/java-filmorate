package ru.yandex.practicum.filmorate.dal.film.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_ID"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getTimestamp("releaseDate").toLocalDateTime().toLocalDate());
        film.setDuration(rs.getLong("duration"));
        film.getMpa().setId(rs.getInt("rating_mpa_ID"));

        return film;
    }
}
