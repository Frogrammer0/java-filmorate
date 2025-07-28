package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmValidator;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmStorage filmStorage;
    FilmValidator validator;

    public FilmController(FilmStorage filmStorage,FilmValidator validator) {
        this.filmStorage = filmStorage;
        this.validator = validator;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        // проверяем выполнение необходимых условий
        log.info("вызван метод create");
        validator.validate(film);
        filmStorage.create(film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        // проверяем необходимые условия
        log.info("вызван метод update");
        return filmStorage.update(newFilm);
    }




}
