package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidLoginException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage inMemoryUserStorage;

    public User create(User user) {
        if (isNoSpaceInLogin(user)) {
            ifUserNameBlank(user);
            log.trace("Пользователь отправлен в хранилище");
            return inMemoryUserStorage.create(user);
        }
        log.trace("Пользователь не создан");
        return null;
    }

    public User update(User user) {
        if (isNoSpaceInLogin(user)) {
            ifUserNameBlank(user);
            log.trace("Пользователь отправлен в хранилище а обновление");
            return inMemoryUserStorage.update(user);
        }
        log.trace("Пользователь не обновлен");
        return null;
    }

    public User delete(Long userId) {
        log.trace("Запрос отправлен на удаление пользователя в хранилище");
        return inMemoryUserStorage.delete(userId);
    }

    public List<User> getAll() {
        log.debug("Отправляем запрос на возврат всех пользователей из хранилища");
        return inMemoryUserStorage.getAll();
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

    //Метод очистки хранилища для целей тестирования
    public void clear() {
        log.trace("Запрошен доступ для очистки хранилища пользователей для целей Тестирования");
        inMemoryUserStorage.clear();
    }

    //Ниже прописана логика по работе с друзьями

    public Long addToFriends(Long userId, Long friendId) {
        log.trace("Начинаем добавление друзей пользователя в хранилище друзей");
        inMemoryUserStorage.addToFriends(userId, friendId);
        return userId;
    }

    public Long removeFromFriends(Long userId, Long friendId) {
        log.trace("Начинаем удаление друга пользователя в хранилище друзей");
        inMemoryUserStorage.removeFromFriends(userId, friendId);
        return userId;
    }

    public List<User> getFriendsListOfUser(Long userId) {
        log.trace("Начинаем возврат списка друзей пользователя");
        return inMemoryUserStorage.getFriendsListOfUser(userId);
    }

    public List<User> getListOfCommonFriends(Long userId, Long otherId) {
        log.trace("Начинаем возврат общего с другим пользователем списка друзей");
        return inMemoryUserStorage.getListOfCommonFriends(userId, otherId);
    }
}
