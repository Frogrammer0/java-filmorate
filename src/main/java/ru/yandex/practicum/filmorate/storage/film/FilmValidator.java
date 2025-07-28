package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
@Component
public class FilmValidator {
    final LocalDate birthdayFilm = LocalDate.parse("1895-12-28");

    public void validate(Film film) {
        validateName(film.getName());
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());
    }


    void validateName(String name) {
        log.info("совершена валидация названия");
        if (name == null || name.isBlank()) {
            log.error("название фильма не введено");
            throw new ValidationException("Название фильма должно быть указано");
        }
    }

    void validateDescription(String description) {
        log.info("совершена валидация описания");
        if (description != null) {
            if (description.length() > 200) {
                log.error("размер описания фильма превышает допустимый размер");
                throw new ValidationException("Описание фильма более 200 символов");
            }
        }
    }

    void validateReleaseDate(LocalDate releaseDate) {
        log.info("совершена валидация даты релиза");
        if (releaseDate == null) {
            log.error("не указана дата релиза");
            throw new ValidationException("Дата релиза не указана");
        } else {
            if (releaseDate.isBefore(birthdayFilm)) {
                log.error("введена неверная дата релиза ");
                throw new ValidationException("Дата релиза слишком ранняя");
            }
        }
    }

    void validateDuration(long duration) {
        log.info("совершена валидация длительности");
        if (duration < 1) {
            log.error("введена неверная длительность фильма");
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }
    }
}
