package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("поиск всех пользователей");
        return userStorage.findAll();
    }

    public Optional<Set<User>> findFriendsByUser(Long userId) {
        log.info("друзья пользователя {}", userId);
        return Optional.of(userStorage.findUserById(userId).getFriends().stream()
                .map(this::findUserById)
                .collect(Collectors.toSet()));
    }

    public User findUserById(Long userId) {
        log.info("поиск пользователя по id");
        return userStorage.findUserById(userId);
    }

    public User create(User user) {
        log.info("создание пользователя");
        return userStorage.create(user);
    }

    public User update(User newUser) {
        log.info("обновление пользователя");
        return userStorage.update(newUser);
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("пользователь с id= {} добавлен в друзья пользователю с id= {}", userId, friendId);
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("пользователь с id = {} удален у пользователя с id = {}", userId, friendId);
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Set<Long> showCommonFriends(Long userId, Long friendId) {
        log.info("показ общих друзей у пользователей с id {} и {}", userId, friendId);
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        return user.getFriends().stream()
                .filter(id -> friend.getFriends().contains(id))
                .collect(Collectors.toSet());
    }


}
