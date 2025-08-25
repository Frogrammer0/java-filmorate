package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setLogin("testLogin");
        user.setName("Test Name");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateAndFindUserById() {
        User created = userStorage.create(user);
        Optional<User> found = userStorage.findUserById(created.getId());

        assertThat(found)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(created.getId());
                    assertThat(u.getEmail()).isEqualTo("test@mail.com");
                    assertThat(u.getLogin()).isEqualTo("testLogin");
                });
    }

    @Test
    void testFindAll() {
        userStorage.create(user);
        List<User> users = userStorage.findAll();
        assertThat(users).isNotEmpty();
    }

    @Test
    void testUpdateUser() {
        User created = userStorage.create(user);
        created.setLogin("newLogin");
        created.setName("New Name");
        created.setEmail("new@mail.com");

        userStorage.update(created);

        Optional<User> updated = userStorage.findUserById(created.getId());

        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getLogin()).isEqualTo("newLogin");
                    assertThat(u.getName()).isEqualTo("New Name");
                    assertThat(u.getEmail()).isEqualTo("new@mail.com");
                });
    }

    @Test
    void testIsMailExist() {
        userStorage.create(user);
        assertThat(userStorage.isMailExist("test@mail.com")).isTrue();
        assertThat(userStorage.isMailExist("no@mail.com")).isFalse();
    }

    @Test
    void testIsUserExist() {
        User created = userStorage.create(user);
        assertThat(userStorage.isUserExist(created.getId())).isTrue();
        assertThat(userStorage.isUserExist(999)).isFalse();
    }
}
