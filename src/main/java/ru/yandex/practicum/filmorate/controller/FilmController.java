package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmValidator;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/*Добавьте методы, позволяющие пользователям добавлять друг друга в друзья, получать список общих
друзей и лайкать фильмы. Проверьте, что все они работают корректно.

       Убедитесь, что ваше приложение возвращает корректные HTTP-коды:
        400 — если ошибка валидации: ValidationException;
404 — для всех ситуаций, если искомый объект не найден;
500 — если возникло исключение.*/

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmService filmService;
    FilmValidator validator;
    UserStorage userStorage;

    public FilmController(FilmService filmService,FilmValidator validator, UserStorage userStorage) {
        this.filmService = filmService;
        this.validator = validator;
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    @ResponseBody
    public Optional<Film> findFilmById(@PathVariable Long filmId){
        return Optional.ofNullable(filmService.findFilmById(filmId));
    }

    @GetMapping("/popular?count={count}")
    @ResponseBody
    public Set<Film> findPopular(@PathVariable long count){
        return filmService.getTopFilm(count).stream()
                .map(id -> filmService.findFilmById(id))
                .collect(Collectors.toSet());
    }

    @PostMapping
    @ResponseBody
    public Film create(@RequestBody Film film) {
        // проверяем выполнение необходимых условий
        log.info("вызван метод create");
        validator.validate(film);
        filmService.create(film);
        return film;
    }

    @PutMapping
    @ResponseBody
    public Film update(@RequestBody Film newFilm) {
        // проверяем необходимые условия
        log.info("вызван метод update");
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId} ")
    public void addLike(@PathVariable long id, long userId){
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, long userId){
        filmService.deleteLike(userId, id);
    }







}
