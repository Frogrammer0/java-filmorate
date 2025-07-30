package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final UserValidator validator = new UserValidator();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        boolean isMailExist = users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(mail -> mail.equals(user.getEmail()));
        if (isMailExist) {
            log.error("введен уже использующийся имейл: {}", user.getEmail());
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


    public User update(User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            // если пользователь найден и все условия соблюдены, обновляем его содержимое
            if (newUser.getName() != null) {
                validator.validateName(newUser);
                oldUser.setName(newUser.getName());
                log.info("изменено имя пользователя");
            }
            if (newUser.getLogin() != null) {
                validator.validateLogin(newUser.getLogin());
                oldUser.setLogin(newUser.getLogin());
                log.info("изменен логин пользователя");
            }
            if (newUser.getEmail() != null) {
                validator.validateEmail(newUser.getEmail());
                oldUser.setEmail(newUser.getEmail());
                log.info("изменена почта пользователя");
            }
            if (newUser.getBirthday() != null) {
                validator.validateBirthday(newUser.getBirthday());
                oldUser.setBirthday(newUser.getBirthday());
                log.info("изменена дата рождения пользователя");
            }

            return oldUser;
        }
        log.error("пользователь с введенным id не найден");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }


    // вспомогательный метод для генерации идентификатора нового пользователя
    private Long getNextId() {
        log.info("создан id");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }



    public User findUserById(Long id) {
        if (!isUserExist(id)) {
            throw new NotFoundException("Пользователь с id = " + id + "не найдем");
        }
        return users.get(id);
    }

    private boolean isUserExist(Long id) {
        return users.containsKey(id);
    }
}
