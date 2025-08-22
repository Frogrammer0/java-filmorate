package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
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

    public Film update(Film newFilm) {
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
        return oldFilm;
    }


    public Film findFilmById(Integer id) {
        return films.get(id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        films.get(filmId).getLikes().add(userId);
    }

    public boolean isFilmExist(Integer id) {
        return films.containsKey(id);
    }


    public boolean isFilmExist(String name) {
        return films.values()
               .stream()
               .map(Film::getName)
               .anyMatch(f -> f.equals(name));
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        films.get(filmId).getLikes().remove(userId);
    }

    public List<Film> getTopFilm(long count) {

        return findAll().stream()
                .sorted(Comparator.comparingLong((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }


    // вспомогательный метод для генерации идентификатора нового пользователя
    private Integer getNextId() {
        log.info("вызван метод создания id");
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
