package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class, UserService.class})
class UserServiceTest {

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setLogin("login1");
        user1.setName("User One");
        user1.setEmail("one@mail.com");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        user2 = new User();
        user2.setLogin("login2");
        user2.setName("User Two");
        user2.setEmail("two@mail.com");
        user2.setBirthday(LocalDate.of(1999, 5, 5));
    }

    @Test
    void testCreateUser() {
        User created = userService.create(user1);
        assertThat(created.getId()).isNotNull();
    }

    @Test
    void testCreateUserWithDuplicateEmailThrowsException() {
        userService.create(user1);
        user2.setEmail("one@mail.com");

        assertThatThrownBy(() -> userService.create(user2))
                .isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void testFindAll() {
        userService.create(user1);
        userService.create(user2);

        assertThat(userService.findAll()).hasSize(2);
    }

    @Test
    void testFindUserById() {
        User created = userService.create(user1);
        User found = userService.findUserById(created.getId());
        assertThat(found.getEmail()).isEqualTo("one@mail.com");
    }

    @Test
    void testFindUserByIdThrowsNotFoundException() {
        assertThatThrownBy(() -> userService.findUserById(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testUpdateUser() {
        User created = userService.create(user1);
        created.setName("Updated Name");
        User updated = userService.update(created);
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void testUpdateUserWithIdZeroThrowsValidationException() {
        user1.setId(0);
        assertThatThrownBy(() -> userService.update(user1))
                .isInstanceOf(ValidationException.class);
    }

}
