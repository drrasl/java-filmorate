package ru.yandex.practicum.filmorate.dal.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.user.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Repository
public class UserDbRepository implements UserStorage {

    protected final JdbcTemplate jdbc;
    protected final UserRowMapper mapper;

    @Autowired
    public UserDbRepository(JdbcTemplate jdbc, UserRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    private static final String INSERT_QUERY = "INSERT INTO userStorage(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE userStorage SET email = ?, login = ?, name = ?, " +
            "birthday = ? WHERE user_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM userStorage WHERE user_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM userStorage";
    private static final String DELETE_ALL_QUERY = "DELETE FROM userStorage";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM userStorage WHERE user_ID = ?";
    private static final String INSERT_FRIEND = "INSERT INTO friendsStorage (user_ID, user_friend_ID) VALUES (?, ?)";
    private static final String REMOVE_FRIEND = "DELETE FROM friendsStorage WHERE user_ID = ? AND user_friend_ID = ?";
    private static final String SELECT_LIST_OF_USER_FRIENDS = "SELECT * FROM userStorage " +
            "WHERE user_ID IN (SELECT fs.user_friend_ID FROM friendsStorage AS fs WHERE fs.user_ID = ?)";
    private static final String SELECT_OF_COMMON_FRIENDS_OF_USERS = "SELECT * FROM userStorage " +
            "WHERE user_ID IN (SELECT fs.user_friend_ID FROM friendsStorage AS fs " +
            "WHERE fs.user_ID = ? OR fs.user_ID = ? GROUP BY fs.user_friend_ID HAVING COUNT(fs.user_ID) = 2)";

    @Transactional
    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setTimestamp(4, Timestamp.valueOf(user.getBirthday().atStartOfDay()));
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new DuplicatedDataException("Email или логин уже заняты");
        }
        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new DataNotFoundException("Не удалось получить ID созданного пользователя");
        }
        user.setId(id);
        log.debug("Пользователь добавлен в базу данных. Id = {}", id);
        return user;
    }

    @Transactional
    @Override
    public User update(User user) {
        try {
            int rowsUpdated = jdbc.update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(),
                    Timestamp.valueOf(user.getBirthday().atStartOfDay()), user.getId());
            if (rowsUpdated == 0) {
                throw new DataNotFoundException("Не удалось обновить данные, пользователь с id = " + user.getId() + " не найден");
            }
        } catch (DuplicateKeyException e) {
            throw new DuplicatedDataException("Email или логин уже заняты");
        }
        log.debug("Пользователь найден и обновлен в хранилище");
        return user;
    }

    @Override
    public User delete(Long userId) {
        User user = getUserById(userId);
        int rowsUpdated = jdbc.update(DELETE_QUERY, userId);
        if (rowsUpdated == 0) {
            throw new DataNotFoundException("Не удалось удалить пользователя с id = " + userId);
        }
        log.debug("Пользователь найден и удален из хранилища");
        return user;
    }

    @Override
    public List<User> getAll() {
        log.debug("Возвращаем всех пользователей из хранилища");
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    //Метод очистки хранилища для целей тестирования
    @Override
    public void clear() {
        log.trace("Очищаем хранилище пользователей для целей Тестирования");
        int rowsUpdated = jdbc.update(DELETE_ALL_QUERY);
        log.debug("Удалено {} записей", rowsUpdated);
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }
        try {
            log.debug("Возвращаем пользователя с id = {} из хранилища", userId);
            return jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, userId);
        } catch (EmptyResultDataAccessException ex) {
            log.debug("Фильм не найден - вернем null в сервис");
            return null;
        }
    }

    //Ниже прописана логика по работе с друзьями

    @Override
    public Long addToFriends(Long userId, Long friendId) {
        try {
            log.debug("Добавим дружбу {} -> {} в таблицу", userId, friendId);
            jdbc.update(INSERT_FRIEND, userId, friendId);
        } catch (DuplicateKeyException ex) {
            log.warn("Попытка добавить существующую дружбу: {} -> {}", userId, friendId);
            throw new DuplicatedDataException("Эти пользователи уже друзья");
        }
        return userId;
    }

    @Override
    public Long removeFromFriends(Long userId, Long friendId) {
        log.debug("Удалим дружбу {} -> {} из таблицы", userId, friendId);
        int rowsDeleted = jdbc.update(REMOVE_FRIEND, userId, friendId);
        if (rowsDeleted == 0) {
            log.debug("Дружба между пользователями {} -> {} не найдена, а значит удалять нечего", userId, friendId);
        }
        return userId;
    }

    //Метод возвратит список всех добавленных друзей, неважно взаимная дружба или нет.
    @Override
    public List<User> getFriendsListOfUser(Long userId) {
        log.debug("Возвращаем список друзей пользователя с id = {}", userId);
        return jdbc.query(SELECT_LIST_OF_USER_FRIENDS, mapper, userId);
    }

    @Override
    public List<User> getListOfCommonFriends(Long userId, Long otherId) {
        log.debug("Возвращаем список общих друзей пользователей {} и {}", userId, otherId);
        return jdbc.query(SELECT_OF_COMMON_FRIENDS_OF_USERS, mapper, userId, otherId);
    }
}
