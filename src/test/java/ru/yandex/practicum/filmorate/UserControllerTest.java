package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    private UserController controller;
    private final UserService userService;
    private final UserValidator validator;
    private final FilmService filmService;

    @Autowired
    public UserControllerTest(UserService userService, UserValidator validator, FilmService filmService) {
        this.userService = userService;
        this.validator = validator;
        this.filmService = filmService;
    }

    @BeforeEach
    void setUp() {
        controller = new UserController(userService, validator, filmService);
    }

    @Test
    void shouldCreateUserWithValidData() {
        User user = new User();
        user.setEmail("test1@example.com");
        user.setLogin("testuser");
        user.setName("Test");
        user.setBirthday(LocalDate.parse("2000-01-01"));

        User createdUser = controller.create(user);

        assertEquals(2, createdUser.getId());
        assertEquals("test1@example.com", createdUser.getEmail());
    }

    @Test
    void shouldUpdateUserWithValidData() {
        User user = new User();
        user.setEmail("test2@example.com");
        user.setLogin("testuser");
        user.setName("Test");
        user.setBirthday(LocalDate.parse("2000-01-01"));

        User createdUser = controller.create(user);

        User newUser = new User();
        newUser.setName("Update");
        newUser.setLogin("updateLogin");
        newUser.setEmail("update@example.com");
        newUser.setId(1L);

        User updateUser = controller.update(newUser);

        assertEquals(1, updateUser.getId());
        assertEquals("update@example.com", updateUser.getEmail());
    }

    @Test
    void shouldFailWithoutAtInEmail() {
        User user = new User();
        user.setEmail("invalidemail.com");
        user.setLogin("testuser");
        user.setName("Test");
        user.setBirthday(LocalDate.parse("2000-01-01"));

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Неверный формат адреса почты: invalidemail.com", ex.getMessage());
    }

    @Test
    void shouldFailWithEmptyEmail() {
        User user = new User();
        user.setEmail(" ");
        user.setLogin("testuser");
        user.setName("Test");
        user.setBirthday(LocalDate.parse("2000-01-01"));

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(user));
        assertEquals("Имейл должен быть указан", ex.getMessage());
    }

    @Test
    void shouldFailWithDuplicateEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test");
        user.setBirthday(LocalDate.parse("2000-01-01"));

        User createdUser = controller.create(user);

        User dUser = new User();
        dUser.setEmail("test@example.com");
        dUser.setLogin("testuser1");
        dUser.setName("Test1");
        dUser.setBirthday(LocalDate.parse("2000-01-01"));

        DuplicatedDataException ex = assertThrows(DuplicatedDataException.class, () -> controller.create(dUser));
        assertEquals("Этот имейл уже используется", ex.getMessage());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        User user = new User();
        user.setId(999L);
        user.setLogin("newlogin");
        user.setEmail("new@example.com");
        user.setName("New Name");
        user.setBirthday(LocalDate.parse("2000-01-01"));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> controller.update(user));
        assertTrue(ex.getMessage().contains("не найден"));
    }


}
