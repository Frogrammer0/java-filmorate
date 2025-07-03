package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        // проверяем выполнение необходимых условий
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("название фильма не введено");
            throw new ValidationException("Название фильма должно быть указано");
        }
        if (film.getDescription().length() > 200) {
            log.error("размер описания фильма превышает допустимый размер");
            throw new ValidationException("Описание фильма более 200 символов");
        }

        if (film.getReleaseDate() == null) {
            log.error("не указана дата релиза");
            throw new ValidationException("Дата релиза не указана");
        } else {
            film.setInstantReleaseDate(parseToInstant(film.getReleaseDate()));
            if (film.getInstantReleaseDate().isBefore(film.getBirthdayFilm())) {
                log.error("введена неверная дата релиза");
                throw new ValidationException("Дата релиза слишком ранняя");
            }
        }
        if (film.getDuration() < 0) {
            log.error("введена отрицательная длительность фильма");
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }

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
        if (newFilm.getId() == 0) {
            log.error("не указан id фильма");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());


            // если фильм найден и все условия соблюдены, обновляем его содержимое
            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
                log.info("изменено название фильма");
            }
            if (newFilm.getDuration() != 0) {
                oldFilm.setDuration(newFilm.getDuration());
                log.info("изменена длительность фильма");
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
                log.info("изменено описание фильма");
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                oldFilm.setInstantReleaseDate(parseToInstant(newFilm.getReleaseDate()));
                log.info("изменена дата релиза фильма");
            }
            return oldFilm;
        }
        log.error("не найден фильм с указанным id");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = films.keySet()
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
