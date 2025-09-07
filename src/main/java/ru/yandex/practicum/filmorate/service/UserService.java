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
import java.util.List;


@Service
@Slf4j
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("db") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("поиск всех пользователей");
        return userStorage.findAll();
    }

    public List<User> findFriendsByUser(Integer userId) {
        User user = getUserOrThrow(userId);
        log.info("друзья пользователя {} = {}", userId, user.getFriends());
        return userStorage.findFriendsByUser(userId);
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
        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.addFriendship(userId, friendId);
        log.info("пользователь с id = {} добавлен в друзья пользователю с id = {}", friendId, userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        log.info("пользователь с id = {} удален у пользователя с id = {}", friendId, userId);
        userStorage.removeFriendship(userId, friendId);
    }

    public List<User> showCommonFriends(Integer userId, Integer friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        log.info("показ общих друзей у пользователей с id {} и {}", userId, friendId);
        return userStorage.showCommonFriends(userId, friendId);
    }

    private User getUserOrThrow(Integer id) {
        return userStorage.findUserById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + id + " не найден")
        );
    }


}
