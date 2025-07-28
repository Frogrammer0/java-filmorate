package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    UserStorage userStorage;


    public void addFriend(Long userId, Long friendId){
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

    public Set<Long> showMutualFriends(Long userId, Long friendId) {
        log.info("показ общих друзей у пользователей с id {} и {}", userId, friendId);
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        return user.getFriends().stream()
                .filter(id -> friend.getFriends().contains(id))
                .collect(Collectors.toSet());
    }


}
