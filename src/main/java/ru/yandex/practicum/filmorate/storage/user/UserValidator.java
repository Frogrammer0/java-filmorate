package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

@Slf4j
@Component
public class UserValidator {
    public void validate(User user) {
        // проверяем выполнение необходимых условий
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateName(user);
        validateBirthday(user.getBirthday());
    }


    void validateEmail(String email) {
        log.info("валидация почты");

        if (email == null || email.isBlank()) {
            log.error("Не указана почта");
            throw new ValidationException("Имейл должен быть указан");
        }

        if (!email.contains("@")) {
            log.error("почта введена в неверном формате");
            throw new ValidationException("Неверный формат адреса почты: " + email);
        }
    }

    void validateLogin(String login) {
        log.info("валидация логина");
        if (login == null || login.isBlank() || login.contains(" ")) {
            log.error("логин {} не введен или содержит пробелы", login);
            throw new ValidationException("Логин не может быть пустым и не может содержать пробелы: " + login);
        }
    }

    void validateBirthday(LocalDate birthday) {
        log.info("валидация даты рождения");
        if (birthday == null) {
            throw new ValidationException("дата рождения не указана");
        } else {
            log.info("пользователю присвоен Birthday");
            if (birthday.isAfter(LocalDate.now())) {
                log.error("неверная дата рождения");
                throw new ValidationException("Дата рождения не может быть позже настоящего момента: " + birthday);
            }
        }
    }

    void validateName(User user) {
        log.info("валидация имени");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("логин установлен в качестве имени пользователя");
        }
    }




}
