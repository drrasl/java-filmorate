package ru.yandex.practicum.filmorate.dal.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.user.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbRepository.class, UserRowMapper.class})
class UserDbRepositoryTest {
    private final UserDbRepository userDbRepository;

    @Test
    public void createUserTest() {
        User user = User.builder()
                .email("email@email.com")
                .login("emailman")
                .name("Vasily")
                .birthday(LocalDate.of(2000, 8, 22))
                .build();
        userDbRepository.create(user);

        User user1 = userDbRepository.getUserById(1L);
        assertEquals(user, user1, "Пользователи не совпадают");
    }
}