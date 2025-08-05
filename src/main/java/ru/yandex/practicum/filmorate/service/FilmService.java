package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        log.info("поиск всех фильмов");
        return filmStorage.findAll();
    }

    public Film findFilmById(Long id) {
        log.info("поиск фильма по id");
        return getFilmOrThrow(id);
    }

    public Film create(Film film) {
        log.info("создание фильма");

        if (filmStorage.isFilmExist(film.getName())) {
            log.error("добавлен существующий фильм");
            throw new DuplicatedDataException("Фильм с таким названием уже добавлен");
        }
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.info("обновление фильма");

        if (newFilm.getId() == 0) {
            log.error("не указан id фильма");
            throw new ValidationException("Id должен быть указан");
        }
        return filmStorage.update(newFilm).orElseThrow(
                () -> new NotFoundException("не удалось обновить фильм " + newFilm.getName())
        );
    }

    public void addLike(Long userId, Long filmId) {
        log.info("пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
        User user = getUserOrThrow(userId);
        Film film = getFilmOrThrow(filmId);

        user.getLikes().add(filmId);
        film.getLikes().add(userId);
    }

    public void deleteLike(Long userId, Long filmId) {
        log.info("пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
        User user = getUserOrThrow(userId);
        Film film = getFilmOrThrow(filmId);

        user.getLikes().remove(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getTopFilm(long count) {
        log.info("запрошен топ {} фильмов", count);
        if (count <= 0) {
            throw new ValidationException("диапазон топ-подборки должен быть больше нуля");
        }
        return filmStorage.getTopFilm(count);
    }

    private Film getFilmOrThrow(long id) {
        return filmStorage.findFilmById(id).orElseThrow(
                () -> new NotFoundException("фильм с " + id + " не найден")
        );
    }

    private User getUserOrThrow(long id) {
        return userStorage.findUserById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + id + " не найдем")
        );
    }
}
