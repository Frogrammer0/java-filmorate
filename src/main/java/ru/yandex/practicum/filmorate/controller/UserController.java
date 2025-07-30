package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserValidator;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;



@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;
    UserValidator validator;
    FilmService filmService;

    public UserController(UserService userService, UserValidator validator, FilmService filmService) {
        this.userService = userService;
        this.validator =  validator;
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseBody
    public Collection<User> findAll() {
        log.info("GET /users найти всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public Optional<User> findById(@PathVariable long userId) {
        log.info("GET /users/{userId} найти пользователя по id");
        return Optional.ofNullable(userService.findUserById(userId));
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public Optional<Set<User>> findFriendsByUser(@PathVariable long id) {
        log.info("GET /users/{}/friends найти друзей пользователя", id);
        return userService.findFriendsByUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseBody
    public Set<User> findCommonFriends(@PathVariable long id,@PathVariable long otherId) {
        log.info("GET /users/{}/friends/common найти общих друзей пользователя", id);
        return userService.showCommonFriends(id, otherId).stream()
                .map(userId -> userService.findUserById(userId))
                .collect(Collectors.toSet());
    }

    @PostMapping
    @ResponseBody
    public User create(@RequestBody User user) {
        log.info("POST /users создать пользователя");
        validator.validate(user);
        return userService.create(user);
    }

    @PutMapping
    @ResponseBody
    public User update(@RequestBody User newUser) {
        log.info("PUT /users обновить пользователя");
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("PUT /users/{}/friends добавить в друзья пользователя", id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("PUT users/{}/friends/ удалить из друзей", id);
        userService.removeFriend(id, friendId);
    }

}
