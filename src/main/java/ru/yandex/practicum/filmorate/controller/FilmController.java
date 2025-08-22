package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmValidator;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmService filmService;
    FilmValidator validator;

    public FilmController(FilmService filmService,FilmValidator validator) {
        this.filmService = filmService;
        this.validator = validator;
    }

    @GetMapping
    @ResponseBody
    public Collection<Film> findAll() {
        log.info("GET /films - получить все фильмы");
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    @ResponseBody
    public Film findFilmById(@PathVariable int filmId) {
        log.info("GET /films/{filmId} - получить фильм по id");
        return filmService.findFilmById(filmId);
    }

    @GetMapping("/popular")
    @ResponseBody
    public List<Film> findPopular(@RequestParam(defaultValue = "10") long count) {
        log.info("GET /films/popular - топ популярных фильмов");
        return filmService.getTopFilm(count);
    }


    @PostMapping
    @ResponseBody
    public Film create(@RequestBody Film film) {
        log.info("POST /films создание фильма =  {}", film);
        validator.validate(film);
        return filmService.create(film);
    }

    @PutMapping
    @ResponseBody
    public Film update(@RequestBody Film newFilm) {
        log.info("PUT /films обновление фильма");
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT /films/{}/like/ добавить лайк", id);
        filmService.addLike(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLike(@PathVariable int id,@PathVariable int userId) {
        log.info("DELETE /films/{}/like удалить лайк", id);
        filmService.deleteLike(userId, id);
        return ResponseEntity.ok().build();
    }

}
