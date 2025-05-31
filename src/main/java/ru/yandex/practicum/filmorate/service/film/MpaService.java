package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpas() {
        log.debug("Отправляем запрос на возврат всех рейтингов MPA");
        return mpaStorage.getAllMpas();
    }

    public Mpa getMpaById(Integer mpaId) {
        log.debug("Отправляем запрос на возврат рейтинга MPA по его ID");
        return mpaStorage.getMpaById(mpaId)
                .orElseThrow(() -> new DataNotFoundException("Рейтинг MPA с ID " + mpaId + " не найден"));
    }
}