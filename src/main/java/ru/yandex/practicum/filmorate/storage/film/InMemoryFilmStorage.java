package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidator validator = new FilmValidator();

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {

        // формируем дополнительные данные
        film.setId(getNextId());
        log.info("фильму присвоен id");
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.info("фильм добавлен в базу");

        return film;
    }

    public Optional<Film> update(Film newFilm) {
        // проверяем необходимые условия
        Film oldFilm = films.get(newFilm.getId());
        if (films.containsKey(newFilm.getId())) {
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

        }
        return Optional.ofNullable(oldFilm);
    }


    public Optional<Film> findFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    public boolean isFilmExist(Long id) {
        return films.containsKey(id);
    }


    public boolean isFilmExist(String name) {
        return films.values()
               .stream()
               .map(Film::getName)
               .anyMatch(f -> f.equals(name));
    }

    public List<Film> getTopFilm(long count) {

        return findAll().stream()
                .sorted(Comparator.comparingLong((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
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
