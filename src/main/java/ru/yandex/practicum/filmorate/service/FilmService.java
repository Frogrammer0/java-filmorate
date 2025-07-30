package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        return filmStorage.findFilmById(id);
    }

    public Film create(Film film) {
        log.info("создание фильма");
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.info("обновление фильма");
        return filmStorage.update(newFilm);
    }

    public void addLike(Long userId, Long filmId) {
        log.info("пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);

        user.getLikes().add(filmId);
        film.getLikes().add(userId);
    }

    public void deleteLike(Long userId, Long filmId) {
        log.info("пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);

        user.getLikes().remove(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getTopFilm(long count) {
        log.info("запрошен топ 10 фильмов");
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingLong((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
