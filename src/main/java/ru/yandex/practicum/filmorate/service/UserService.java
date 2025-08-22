package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("db")UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("поиск всех пользователей");
        return userStorage.findAll();
    }

    public Set<User> findFriendsByUser(Integer userId) {
        User user  = getUserOrThrow(userId);
        log.info("друзья пользователя {} = {}", userId, user.getFriends());
        return user.getFriends().stream()
                .map(this::findUserById)
                .collect(Collectors.toSet());
    }

    public User findUserById(Integer userId) {
        log.info("поиск пользователя по id");
        return getUserOrThrow(userId);
    }

    public User create(User user) {
        log.info("создание пользователя");
        if (userStorage.isMailExist(user.getEmail())) {
            log.error("введен уже использующийся имейл: {}", user.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        return userStorage.create(user);
    }

    public User update(User newUser) {
        log.info("обновление пользователя");
        if (newUser.getId() == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }
        return userStorage.update(newUser).orElseThrow(
                () -> new NotFoundException("не удалось обновить пользователя " + newUser.getLogin())
        );
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().add(friendId);
        userStorage.update(user);
        userStorage.addFriendship(userId, friendId);
        log.info("пользователь с id = {} добавлен в друзья пользователю с id = {}", friendId, userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            userStorage.update(user);
            userStorage.removeFriendship(userId, friendId);
            log.info("пользователь с id = {} удален у пользователя с id = {}", friendId, userId);
        }
    }

    public Set<User> showCommonFriends(Integer userId, Integer friendId) {
        log.info("показ общих друзей у пользователей с id {} и {}", userId, friendId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(this::getUserOrThrow)
                .collect(Collectors.toSet());
    }

    private User getUserOrThrow(Integer id) {
        return userStorage.findUserById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + id + " не найден")
        );
    }


}
