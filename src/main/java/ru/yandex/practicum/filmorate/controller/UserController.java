package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Не указана почта");
            throw new ValidationException("Имейл должен быть указан");
        }

        if (!user.getEmail().contains("@")) {
            log.error("почта введена в неверном формате");
            throw new ValidationException("Неверный формат адреса почты");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("логин не введен");
            throw new ValidationException("Логин не может быть пустым и не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("установлено имя пользователя");
        }

        if (user.getBirthday() == null) {
            throw new ValidationException("дата рождения не указана");
        } else {
            user.setInstantBirthday(parseToInstant(user.getBirthday()));
            log.info("пользователю присвоен Birthday");
            if (user.getInstantBirthday().isAfter(Instant.now())) {
                log.error("неверная дата рождения");
                throw new ValidationException("Дата рождениия не может быть позже настоящего момента");
            }
        }

        boolean isMailExist = users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(mail -> mail.equals(user.getEmail()));
        if (isMailExist) {
            log.error("введен уже использующийся имейл");
            throw new DuplicatedDataException("Этот имейл уже используется");
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
        // проверяем необходимые условия
        if (newUser.getId() == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            // если пользователь найден и все условия соблюдены, обновляем его содержимое
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
                log.info("изменено имя пользователя");
            }
            if (newUser.getLogin() != null) {
                oldUser.setLogin(newUser.getLogin());
                log.info("изменен логин пользователя");
            }
            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
                log.info("изменена почта пользователя");
            }
            if (newUser.getBirthday() != null) {
                oldUser.setInstantBirthday(parseToInstant(newUser.getBirthday()));
                oldUser.setBirthday(newUser.getBirthday());
                log.info("изменена дата рождения пользователя");
            }

            return oldUser;
        }
        log.error("пользователь с введеным id не найден");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public static Instant parseToInstant(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
