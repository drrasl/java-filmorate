package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Начато создание объекта пользователя. Получен объект {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Начато обновление объекта пользователя. Получен объект {}", user);
        return userService.update(user);
    }

    @DeleteMapping("/{userId}")
    public User delete(@PathVariable @Positive Long userId) {
        log.debug("Начато удаление пользователя с userId = {}", userId);
        return userService.delete(userId);
    }

    @GetMapping
    public List<User> getAll() {
        log.debug("Начат возврат всех пользователей");
        return userService.getAll();
    }

    //Для целей тестирования
    public UserService getUserService() {
        log.trace("Запрошен доступ для userService для целей Тестирования");
        return userService;
    }

    //Ниже прописана логика по работе с друзьями

    @PutMapping("/{id}/friends/{friendId}")
    public Long addToFriends(@NotNull @Positive @PathVariable("id") Long userId,
                             @NotNull @Positive @PathVariable Long friendId) {
        log.debug("Начато добавление friendId {} в список друзей пользователя id {}", friendId, userId);
        return userService.addToFriends(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Long removeFromFriends(@NotNull @Positive @PathVariable("id") Long userId,
                                  @NotNull @Positive @PathVariable Long friendId) {
        log.debug("Начато удаление friendId {} из списка друзей пользователя id {}", friendId, userId);
        return userService.removeFromFriends(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsListOfUser(@NotNull @Positive @PathVariable("id") Long userId) {
        log.debug("Начат возврат всех друзей пользователя с ID {}", userId);
        return userService.getFriendsListOfUser(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getListOfCommonFriends(@NotNull @Positive @PathVariable("id") Long userId,
                                             @NotNull @Positive @PathVariable Long otherId) {
        log.debug("Начат возврат всех общих друзей пользователя с ID {} и другого пользователя с ID {}", userId, otherId);
        return userService.getListOfCommonFriends(userId, otherId);
    }

}
