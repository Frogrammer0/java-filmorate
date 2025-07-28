package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmValidator;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

/*Добавьте методы, позволяющие пользователям добавлять друг друга в друзья, получать список общих
друзей и лайкать фильмы. Проверьте, что все они работают корректно.
        PUT /users/{id}/friends/{friendId} — добавление в друзья.
        DELETE /users/{id}/friends/{friendId} — удаление из друзей.
        GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
        GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
        PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
        GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.
Убедитесь, что ваше приложение возвращает корректные HTTP-коды:
        400 — если ошибка валидации: ValidationException;
404 — для всех ситуаций, если искомый объект не найден;
500 — если возникло исключение.*/

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmStorage filmStorage;
    FilmValidator validator;
    UserStorage userStorage;

    public FilmController(FilmStorage filmStorage,FilmValidator validator, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.validator = validator;
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{filmId}")
    @ResponseBody
    public Optional<Film> findFilmById(@PathVariable Long filmId){
        return Optional.ofNullable(filmStorage.findFilmById(filmId));
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
