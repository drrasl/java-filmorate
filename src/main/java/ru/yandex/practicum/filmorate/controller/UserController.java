package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private UserHandler userHandler = new UserHandler();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Начато создание объекта пользователя. Получен объект {}", user);
        return userHandler.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Начато обновление объекта пользователя. Получен объект {}", user);
        return userHandler.update(user);
    }

    @GetMapping
    public List<User> getAll() {
        log.debug("Начат возврат всех пользователей");
        return userHandler.getAll();
    }

    //Для целей тестирования
    public UserHandler getUserHandler() {
        log.trace("Запрошен доступ для userHandler для целей Тестирования");
        return userHandler;
    }
}
