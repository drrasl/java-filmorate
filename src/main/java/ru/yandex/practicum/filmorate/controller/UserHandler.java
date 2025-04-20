package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidLoginException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserHandler {

    private final Map<Long, User> userStorage = new HashMap<>();
    private long generatedId = 0;

    public User create(@Valid User user) {
        if (isNoSpaceInLogin(user)) {
            ifUserNameBlank(user);
            user.setId(++generatedId);
            userStorage.put(user.getId(), user);
            log.debug("Объект пользователя добавлен в хранилище");
        }
        return user;
    }

    public User update(@Valid User user) {
        if (isNoSpaceInLogin(user)) {
            ifUserNameBlank(user);
            if (!userStorage.containsKey(user.getId()) | user.getId() == null) {
                log.debug("Запрашиваемый при обновлении пользователь не найден в хранилище");
                throw new DataNotFoundException("Запрашиваемый пользователь: " + user + " не найден");
            }
            userStorage.put(user.getId(), user);
            log.debug("Пользователь найден и обновлен в хранилище");
        }
        return user;
    }

    public List<User> getAll() {
        log.debug("Возвращаем всех пользователей из хранилища");
        return new ArrayList<>(userStorage.values());
    }

    private boolean isNoSpaceInLogin(User user) {
        log.trace("Начинаем проверку на наличие пробелов в логине пользователя");
        String[] words = user.getLogin().split(" ");
        if (words.length > 1) {
            log.trace("Пробелы в Логине обнаружены");
            throw new InvalidLoginException("Логин не может содержать пробелы");

        }
        log.trace("Пробелы в Логине не обнаружены");
        return true;
    }

    private void ifUserNameBlank(User user) {
        log.trace("Начинаем проверку на отсутствие имени пользователя");
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.trace("Имя пользователя пустое, теперь имя пользователя соответствует Логину: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    //Метод для целей тестирования
    protected void clear() {
        log.trace("Запрошен доступ для очистки хранилища пользователей для целей Тестирования");
        userStorage.clear();
        generatedId = 0;
    }
}
