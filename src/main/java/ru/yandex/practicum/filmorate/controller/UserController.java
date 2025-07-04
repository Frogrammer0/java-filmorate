package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        log.info("вызван метод create");

        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateName(user);
        validateBirthday(user.getBirthday());


        boolean isMailExist = users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(mail -> mail.equals(user.getEmail()));
        if (isMailExist) {
            log.error("введен уже использующийся емейл");
            throw new DuplicatedDataException("Этот емейл уже используется");
        }

        // формируем дополнительные данные
        user.setId(getNextId());
        log.info("пользователю присвоен id");


        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.info("пользователь добавлен в базу");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("вызван метод update");
        // проверяем необходимые условия
        if (newUser.getId() == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            // если пользователь найден и все условия соблюдены, обновляем его содержимое
            if (newUser.getName() != null) {
                validateName(newUser);
                oldUser.setName(newUser.getName());
                log.info("изменено имя пользователя");
            }
            if (newUser.getLogin() != null) {
                validateLogin(newUser.getLogin());
                oldUser.setLogin(newUser.getLogin());
                log.info("изменен логин пользователя");
            }
            if (newUser.getEmail() != null) {
                validateEmail(newUser.getEmail());
                oldUser.setEmail(newUser.getEmail());
                log.info("изменена почта пользователя");
            }
            if (newUser.getBirthday() != null) {
                validateBirthday(newUser.getBirthday());
                oldUser.setBirthday(newUser.getBirthday());
                log.info("изменена дата рождения пользователя");
            }

            return oldUser;
        }
        log.error("пользователь с введенным id не найден");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        log.info("создан id");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateEmail(String email) {
        log.info("валидация почты");
        if (email == null || email.isBlank()) {
            log.error("Не указана почта");
            throw new ValidationException("Имейл должен быть указан");
        }

        if (!email.contains("@")) {
            log.error("почта введена в неверном формате");
            throw new ValidationException("Неверный формат адреса почты");
        }
    }

    private void validateLogin(String login) {
        log.info("валидация логина");
        if (login == null || login.isBlank() || login.contains(" ")) {
            log.error("логин не введен");
            throw new ValidationException("Логин не может быть пустым и не может содержать пробелы");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        log.info("валидация даты рождения");
        if (birthday == null) {
            throw new ValidationException("дата рождения не указана");
        } else {
            log.info("пользователю присвоен Birthday");
            if (birthday.isAfter(LocalDate.now())) {
                log.error("неверная дата рождения");
                throw new ValidationException("Дата рождения не может быть позже настоящего момента");
            }
        }
    }

    private void validateName(User user) {
        log.info("валидация имени");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("логин установлен в качестве имени пользователя");
        }
    }
}
