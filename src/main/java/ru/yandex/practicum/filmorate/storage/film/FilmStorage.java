package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Optional<Film> update(Film newFilm);

    Optional<Film> findFilmById(Long id);

    List<Film> getTopFilm(long count);

    boolean isFilmExist(Long id);

    boolean isFilmExist(String name);
}
