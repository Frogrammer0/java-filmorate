package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    Optional<User> update(User newUser);

    Collection<User> findAll();

    Optional<User> findUserById(Integer id);

    void removeFriendship(Integer userId, Integer friendId);

    boolean isMailExist(String mail);

    boolean isUserExist(Integer id);

    void addFriendship(Integer userId, Integer friendId);

    void addLike(Integer filmId, Integer userId);
}
