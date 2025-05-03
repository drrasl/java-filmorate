package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User delete(Long userId);

    List<User> getAll();

    void clear();

    User getUserById(Long userId);

    Long addToFriends(Long userId, Long friendId);

    Long removeFromFriends(Long userId, Long friendId);

    List<User> getFriendsListOfUser(Long userId);

    List<User> getListOfCommonFriends(Long userId, Long otherId);

}
