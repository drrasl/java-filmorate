package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    public List<Mpa> getAllMpas();

    public Optional<Mpa> getMpaById(Integer mpaId);
}
