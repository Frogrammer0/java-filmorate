package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserValidator;
import java.util.Collection;
import java.util.Optional;

/*Добавьте методы, позволяющие пользователям добавлять друг друга в друзья, получать список общих
друзей и лайкать фильмы. Проверьте, что все они работают корректно.

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
@RequestMapping("/users")
public class UserController {
    UserService userService;
    UserValidator validator;
    FilmService filmService;

    public UserController(UserService userService, UserValidator validator, FilmService filmService){
        this.userService = userService;
        this.validator =  validator;
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseBody
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public Optional<User> findById(@PathVariable long userId) {
        return Optional.ofNullable(userService.findUserById(userId));
    }

    @PostMapping
    @ResponseBody
    public User create(@RequestBody User user) {
        log.info("вызван метод create");
        validator.validate(user);
        return userService.create(user);
    }

    @PutMapping
    @ResponseBody
    public User update(@RequestBody User newUser) {
        log.info("вызван метод update");
        return userService.update(newUser);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable long userId, long friendId) {
        log.info("вызван метод addFriend");
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable long userId, long friendId){

    }







}
