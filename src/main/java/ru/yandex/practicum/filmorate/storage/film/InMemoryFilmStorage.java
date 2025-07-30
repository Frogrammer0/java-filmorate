package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidator validator = new FilmValidator();

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {
        // проверяем выполнение необходимых условий
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

    public Film update(Film newFilm) {
        // проверяем необходимые условия
        if (newFilm.getId() == 0) {
            log.error("не указан id фильма");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            // если фильм найден и все условия соблюдены, обновляем его содержимое
            if (newFilm.getName() != null) {
                validator.validateName(newFilm.getName());
                oldFilm.setName(newFilm.getName());
                log.info("изменено название фильма");
            }
            if (newFilm.getDuration() != 0) {
                validator.validateDuration(newFilm.getDuration());
                oldFilm.setDuration(newFilm.getDuration());
                log.info("изменена длительность фильма");
            }
            if (newFilm.getDescription() != null) {
                validator.validateDescription(newFilm.getDescription());
                oldFilm.setDescription(newFilm.getDescription());
                log.info("изменено описание фильма");
            }
            if (newFilm.getReleaseDate() != null) {
                validator.validateReleaseDate(newFilm.getReleaseDate());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                log.info("изменена дата релиза фильма");
            }
            return oldFilm;
        }

        log.error("не найден фильм с указанным id");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }


    public Film findFilmById(Long id) {
        if (!ifFilmExist(id)) {
            throw new NotFoundException("фильм с id" + id + "не найден");
        }
        return films.get(id);
    }

    private boolean ifFilmExist(Long id) {
        return films.containsKey(id);
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
}
