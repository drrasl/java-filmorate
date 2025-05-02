package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> userStorage = new HashMap<>();
    private final Map<Long, Set<Long>> friendsStorage = new HashMap<>(); // <Id пользователь, Сет из айди его друзей>

    private long generatedId = 0;

    @Override
    public User create(User user) {
        user.setId(++generatedId);
        userStorage.put(user.getId(), user);
        log.debug("Объект пользователя добавлен в хранилище");
        return user;
    }

    @Override
    public User update(User user) {
        if (!userStorage.containsKey(user.getId()) | user.getId() == null) {
            log.debug("Запрашиваемый при обновлении пользователь не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый пользователь: " + user + " не найден");
        }
        userStorage.put(user.getId(), user);
        log.debug("Пользователь найден и обновлен в хранилище");
        return user;
    }

    @Override
    public User delete(Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.debug("Запрашиваемый для удаления пользователь не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый пользователь с ID: " + userId + " не найден");
        }
        User userToDelete = userStorage.get(userId);
        userStorage.remove(userId);
        log.debug("Пользователь найден и Удален из хранилища");
        return userToDelete;
    }

    @Override
    public List<User> getAll() {
        log.debug("Возвращаем всех пользователей из хранилища");
        return new ArrayList<>(userStorage.values());
    }

    //Метод очистки хранилища для целей тестирования
    @Override
    public void clear() {
        log.trace("Очищаем хранилище пользователей для целей Тестирования");
        userStorage.clear();
        generatedId = 0;
    }

    //Ниже прописана логика по работе с друзьями

    @Override
    public Long addToFriends(Long userId, Long friendId) {
        validationOfUserAndFriend(userId, friendId);
        log.trace("Добавляем пользователю с Id {} в список друзей пользователя с Id {}", userId, friendId);
        if (!friendsStorage.containsKey(userId)) {
            log.debug("Пользователя Id {} еще нет в базе друзей: создадим список друзей и добавим в базу", userId);
            log.trace("Создаем Set<Long> и добавим туда Id {} ", friendId);
            Set<Long> userFriends = new HashSet<>();
            userFriends.add(friendId);
            log.trace("Добавляем пользователя c Id {} в хранилище друзей", userId);
            friendsStorage.put(userId, userFriends);
        } else {
            log.debug("Пользователь с Id {} есть в базе друзей: выберем его и добавим ему нового друга", userId);
            Set<Long> userFriends = friendsStorage.get(userId);
            if (!userFriends.contains(friendId)) {
                log.trace("Добавили пользователю с Id {} нового  друга с Id {} ", userId, friendId);
                userFriends.add(friendId);
            } else {
                throw new DuplicatedDataException("Друг с Id: " + friendId + " уже есть в друзьях у Id: " + userId);
            }
        }
        return userId;
    }

    @Override
    public Long removeFromFriends(Long userId, Long friendId) {
        validationOfUserAndFriend(userId, friendId);
        log.trace("Удаляем друга с Id {} из списка друзей пользователя Id {}", friendId, userId);
        if (!friendsStorage.containsKey(userId)) {
            log.debug("У пользователя Id {} еще нет друзей, удаление не возможно", userId);
        } else {
            log.debug("Пользователь с Id {} есть в базе друзей: выберем его и удалим его друга", userId);
            Set<Long> userFriends = friendsStorage.get(userId);
            if (userFriends.contains(friendId)) {
                log.trace("Удаляем друга с Id {} из друзей пользователя Id {} ", friendId, userId);
                userFriends.remove(friendId);
            } else {
                log.trace("У пользователя Id {} нет друга Id {}, а значит и удалять нечего", userId, friendId);
            }
        }
        return userId;
    }

    @Override
    public List<User> getFriendsListOfUser(Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.debug("Пользователя с указанным Id {} не найдено", userId);
            throw new DataNotFoundException("Пользователь с Id: " + userId + " не найден");
        }
        if (!friendsStorage.containsKey(userId)) {
            log.debug("У пользователя Id {} еще нет друзей", userId);
            return List.of();
        }
        Set<Long> userFriends = friendsStorage.get(userId);
        List<User> friendsOfUser = userFriends.stream()
                .filter(Objects::nonNull)
                .map(id -> userStorage.get(id))
                .toList();
        return friendsOfUser;
    }

    @Override
    public List<User> getListOfCommonFriends(Long userId, Long otherId) {
        validationOfUserAndFriend(userId, otherId);
        if (!friendsStorage.containsKey(userId) | !friendsStorage.containsKey(otherId)) {
            log.debug("У одного из пользователей (или у двух) нет друзей => нет общих друзей");
            throw new DataNotFoundException("У пользователей нет общих друзей");
        }
        Set<Long> userFriends = friendsStorage.get(userId);
        Set<Long> otherUserFriends = friendsStorage.get(otherId);
        Set<User> friendsOfUser = userFriends.stream()
                .filter(Objects::nonNull)
                .map(userStorage::get)
                .collect(Collectors.toSet());
        Set<User> friendsOfOtherUser = otherUserFriends.stream()
                .filter(Objects::nonNull)
                .map(userStorage::get)
                .collect(Collectors.toSet());
        friendsOfUser.retainAll(friendsOfOtherUser);
        return new ArrayList<>(friendsOfUser);
    }

    private void validationOfUserAndFriend(Long userId, Long friendId) {
        if (!userStorage.containsKey(userId)) {
            log.debug("Пользователя с указанным Id {} не найдено", userId);
            throw new DataNotFoundException("Пользователь с Id: " + userId + " не найден");
        }
        if (!userStorage.containsKey(friendId)) {
            log.debug("Друга (или другого пользователя) с указанным Id {} не найдено", friendId);
            throw new DataNotFoundException("Друг (или пользователь) с Id: " + userId + " не найден");
        }
    }
}
