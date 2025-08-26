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
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;


    @Autowired
    public FilmService(@Qualifier("db") FilmStorage filmStorage, @Qualifier("db") UserStorage userStorage, @Qualifier("db") MpaStorage mpaStorage,
                       @Qualifier("db") GenreStorage genreStorage) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> findAll() {
        log.info("поиск всех фильмов");
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer id) {
        log.info("поиск фильма по id");
        return getFilmOrThrow(id);
    }

    public Film create(Film film) {
        log.info("создание фильма в filmService = {}", film);
        if (film.getId() != null && filmStorage.isFilmExist(film.getId())) {
            log.error("добавлен существующий фильм с существующим id");
            throw new DuplicatedDataException("Фильм с таким id уже добавлен");
        }
        if (!film.getGenres().isEmpty()) {
            genreStorage.validateGenre(film.getGenres().get(0).getId());
        }
        mpaStorage.validateMpa(film.getMpa().getId());

        Film createdFilm = filmStorage.create(film);
        genreStorage.saveFilmGenres(film);

        return createdFilm;
    }

    public Film update(Film newFilm) {
        log.info("обновление фильма в FilmService");

        if (newFilm.getId() == 0) {
            log.error("не указан id фильма");
            throw new ValidationException("Id должен быть указан");
        }

        if (!newFilm.getGenres().isEmpty()) {
            genreStorage.validateGenre(newFilm.getGenres().get(0).getId());
        }
        if (newFilm.getMpa() != null) {
            mpaStorage.validateMpa(newFilm.getMpa().getId());
        }
        filmStorage.update(newFilm);

        return getFilmOrThrow(newFilm.getId());
    }

    public void addLike(Integer userId, Integer filmId) {
        getUserOrThrow(userId);
        filmStorage.findFilmById(filmId);

        log.info("пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer userId, Integer filmId) {
        getUserOrThrow(userId);
        filmStorage.findFilmById(filmId);

        log.info("пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
        filmStorage.removeLike(filmId, userId);
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

    private Film getFilmOrThrow(Integer id) {
        return filmStorage.findFilmById(id).orElseThrow(
                () -> new NotFoundException("Фильм с id=" + id + " не найден")
        );

    }


}
