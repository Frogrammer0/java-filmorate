package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;


    @Autowired
    public FilmService(@Qualifier("db") FilmStorage filmStorage, @Qualifier("db") UserStorage userStorage,@Qualifier("db") MpaStorage mpaStorage) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
    }

    public Collection<Film> findAll() {
        log.info("поиск всех фильмов");
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer id) {
        log.info("поиск фильма по id");
        return filmStorage.findFilmById(id);
    }

    public Film create(Film film) {
        log.info("создание фильма в filmService = {}", film);

        if (filmStorage.isFilmExist(film.getId())) {
            log.error("добавлен существующий фильм");
            throw new DuplicatedDataException("Фильм с таким id уже добавлен");
        }
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.info("обновление фильма в FilmService");

        if (newFilm.getId() == 0) {
            log.error("не указан id фильма");
            throw new ValidationException("Id должен быть указан");
        }
        return filmStorage.update(newFilm);
    }

    public void addLike(Integer userId, Integer filmId) {
        log.info("пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
        User user = getUserOrThrow(userId);
        Film film = filmStorage.findFilmById(filmId);

        user.getLikes().add(filmId);
        film.getLikes().add(userId);
        userStorage.addLike(filmId, userId);
        filmStorage.addLike(filmId, userId);


        filmStorage.update(film);
        userStorage.update(user);
    }

    public void deleteLike(Integer userId, Integer filmId) {
        log.info("пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
        User user = getUserOrThrow(userId);
        Film film = filmStorage.findFilmById(filmId);

        user.getLikes().remove(filmId);
        film.getLikes().remove(userId);

        filmStorage.removeLike(filmId, userId);

        filmStorage.update(film);
        userStorage.update(user);
    }

    public List<Film> getTopFilm(long count) {
        log.info("запрошен топ {} фильмов", count);
        if (count <= 0) {
            throw new ValidationException("диапазон топ-подборки должен быть больше нуля");
        }
        return filmStorage.getTopFilm(count);
    }


    private User getUserOrThrow(Integer id) {
        return userStorage.findUserById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + id + " не найдем")
        );
    }
}
