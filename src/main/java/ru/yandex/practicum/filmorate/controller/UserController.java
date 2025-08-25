package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserValidator;
import java.util.Collection;
import java.util.List;



@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;
    UserValidator validator;

    public UserController(UserService userService, UserValidator validator) {
        this.userService = userService;
        this.validator =  validator;
    }

    @GetMapping
    @ResponseBody
    public Collection<User> findAll() {
        log.info("GET /users найти всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public User findById(@PathVariable int userId) {
        log.info("GET /users/{userId} найти пользователя по id");
        return userService.findUserById(userId);
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public List<User> findFriendsByUser(@PathVariable int id) {
        log.info("GET /users/{}/friends найти друзей пользователя", id);
        return userService.findFriendsByUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseBody
    public List<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("GET /users/{}/friends/common найти общих друзей пользователя", id);
        return userService.showCommonFriends(id, otherId);
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
    public ResponseEntity<Void> addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("PUT /users/{}/friends добавить в друзья пользователя с id = {}", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("DELETE /users/{}/friends/ удалить из друзей", id);
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

}
