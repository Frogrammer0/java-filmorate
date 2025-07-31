package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("поиск всех пользователей");
        return userStorage.findAll();
    }

    public Set<User> findFriendsByUser(Long userId) {
        log.info("друзья пользователя {}", userId);
        User user  = userStorage.findUserById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + userId + " не найдем")
        );;
        return user.getFriends().stream()
                .map(this::findUserById)
                .collect(Collectors.toSet());
    }

    public User findUserById(Long userId) {
        log.info("поиск пользователя по id");
        return userStorage.findUserById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + userId + " не найдем")
        );
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

    public void addFriend(Long userId, Long friendId) {
        log.info("пользователь с id= {} добавлен в друзья пользователю с id= {}", userId, friendId);
        User user = userStorage.findUserById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + userId + " не найдем")
        );
        User friend = userStorage.findUserById(friendId).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + friendId + " не найдем")
        );
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("пользователь с id = {} удален у пользователя с id = {}", userId, friendId);
        User user = userStorage.findUserById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + userId + " не найдем")
        );
        User friend = userStorage.findUserById(friendId).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + friendId + " не найдем")
        );
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Set<User> showCommonFriends(Long userId, Long friendId) {
        log.info("показ общих друзей у пользователей с id {} и {}", userId, friendId);
        User user = userStorage.findUserById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + userId + " не найдем")
        );
        User friend = userStorage.findUserById(friendId).orElseThrow(
                () -> new NotFoundException("Пользователь с id =" + friendId + " не найдем")
        );
        return user.getFriends().stream()
                .filter(id -> friend.getFriends().contains(id))
                .map(this::findUserById)
                .collect(Collectors.toSet());
    }


}
