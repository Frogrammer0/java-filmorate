package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    Optional<User> update(User newUser);

    Collection<User> findAll();

    Optional<User> findUserById(Long id);

    boolean isMailExist(String mail);

    boolean isUserExist(Long id);
}
