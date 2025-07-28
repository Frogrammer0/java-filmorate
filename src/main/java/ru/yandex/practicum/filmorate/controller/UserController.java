package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserValidator;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserStorage userStorage;
    UserValidator validator;

    public UserController(UserStorage userStorage, UserValidator validator){
        this.userStorage = userStorage;
        this.validator =  validator;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("вызван метод create");
        validator.validate(user);
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("вызван метод update");
        return userStorage.update(newUser);
    }



}
