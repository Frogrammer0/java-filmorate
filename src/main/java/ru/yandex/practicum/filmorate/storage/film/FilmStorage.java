package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Optional<Film> update(Film newFilm);

    Optional<Film> findFilmById(Integer id);

    void addLike(Integer filmId, Integer userId);

    List<Film> getTopFilm(long count);

    boolean isFilmExist(Integer id);

    boolean isFilmExist(String name);

    void removeLike(Integer filmId, Integer userId);
}
