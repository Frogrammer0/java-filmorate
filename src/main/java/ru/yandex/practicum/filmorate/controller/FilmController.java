package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    final LocalDate birthdayFilm = LocalDate.parse("1895-12-28");

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        // проверяем выполнение необходимых условий
        log.info("вызван метод create");

        validateName(film.getName());
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());

        boolean isFilmExist = films.values()
                .stream()
                .map(Film::getName)
                .anyMatch(f -> f.equals(film.getName()));
        if (isFilmExist) {
            log.error("добавлен существующий фильм");
            throw new DuplicatedDataException("Этот фильм уже добавлен");
        }

        // формируем дополнительные данные
        film.setId(getNextId());
        log.info("фильму присвоен id");
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.info("фильм добавлен в базу");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        // проверяем необходимые условия
        log.info("вызван метод update");
        if (newFilm.getId() == 0) {
            log.error("не указан id фильма");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());


            // если фильм найден и все условия соблюдены, обновляем его содержимое
            if (newFilm.getName() != null) {
                validateName(newFilm.getName());
                oldFilm.setName(newFilm.getName());
                log.info("изменено название фильма");
            }
            if (newFilm.getDuration() != 0) {
                validateDuration(newFilm.getDuration());
                oldFilm.setDuration(newFilm.getDuration());
                log.info("изменена длительность фильма");
            }
            if (newFilm.getDescription() != null) {
                validateDescription(newFilm.getDescription());
                oldFilm.setDescription(newFilm.getDescription());
                log.info("изменено описание фильма");
            }
            if (newFilm.getReleaseDate() != null) {
                validateReleaseDate(newFilm.getReleaseDate());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                log.info("изменена дата релиза фильма");
            }
            return oldFilm;
        }
        log.error("не найден фильм с указанным id");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        log.info("вызван метод создания id");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateName(String name) {
        log.info("совершена валидация названия");
        if (name == null || name.isBlank()) {
            log.error("название фильма не введено");
            throw new ValidationException("Название фильма должно быть указано");
        }
    }

    private void validateDescription(String description) {
        log.info("совершена валидация описания");
        if (description != null) {
            if (description.length() > 200) {
                log.error("размер описания фильма превышает допустимый размер");
                throw new ValidationException("Описание фильма более 200 символов");
            }
        }
    }

    private void validateReleaseDate(LocalDate releaseDate) {
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

    private void validateDuration(long duration) {
        log.info("совершена валидация длительности");
        if (duration < 1) {
            log.error("введена неверная длительность фильма");
            throw new ValidationException("Длительность фильма не может быть меньше 1");
        }
    }
}
