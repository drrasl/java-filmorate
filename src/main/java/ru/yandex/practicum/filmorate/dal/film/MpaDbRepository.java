package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MpaDbRepository implements MpaStorage {

    protected final JdbcTemplate jdbc;
    protected final RowMapper<Mpa> mpaRowMapper;

    private static final String SELECT_ALL_MPA = "SELECT * FROM ratingOfFilmByMpa";
    private static final String GET_MPA_BY_ID = "SELECT * FROM ratingOfFilmByMpa WHERE rating_mpa_ID = ?";

    @Override
    public List<Mpa> getAllMpas() {
        log.debug("Возвращаем все рейтинги MPA из хранилища");
        return jdbc.query(SELECT_ALL_MPA, mpaRowMapper);
    }

    @Override
    public Optional<Mpa> getMpaById(Integer mpaId) {
        return jdbc.query(GET_MPA_BY_ID, mpaRowMapper, mpaId)
                .stream()
                .findFirst();
    }
}
