package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("m")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserValidator validator = new UserValidator();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {

        // формируем дополнительные данные
        user.setId(getNextId());
        log.info("пользователю присвоен id");

        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.info("пользователь добавлен в базу");
        return user;
    }


    public Optional<User> update(User newUser) {

        User oldUser = users.get(newUser.getId());
        if (users.containsKey(newUser.getId())) {

            // если пользователь найден и все условия соблюдены, обновляем его содержимое
            if (newUser.getName() != null) {
                validator.validateName(newUser);
                oldUser.setName(newUser.getName());
                log.info("изменено имя пользователя");
            }
            if (newUser.getLogin() != null) {
                validator.validateLogin(newUser.getLogin());
                oldUser.setLogin(newUser.getLogin());
                log.info("изменен логин пользователя");
            }
            if (newUser.getEmail() != null) {
                validator.validateEmail(newUser.getEmail());
                oldUser.setEmail(newUser.getEmail());
                log.info("изменена почта пользователя");
            }
            if (newUser.getBirthday() != null) {
                validator.validateBirthday(newUser.getBirthday());
                oldUser.setBirthday(newUser.getBirthday());
                log.info("изменена дата рождения пользователя");
            }

            if (newUser.getFriends() != null) {
                oldUser.setFriends(newUser.getFriends());
                log.info("обновлены друзья пользователя");
            }

            if (newUser.getLikes() != null) {
                oldUser.setLikes(newUser.getLikes());
                log.info("обновлены лайки пользователя");
            }


        }
        return Optional.ofNullable(oldUser);
    }

    @Override
    public void addFriendship(Integer userId, Integer friendId) {
        users.get(userId).getFriends().add(friendId);
    }

    public void addLike(Integer filmId, Integer userId) {
      users.get(userId).getLikes().add(filmId);
    }


    // вспомогательный метод для генерации идентификатора нового пользователя
    private Integer getNextId() {
        log.info("создан id");
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    public Optional<User> findUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void removeFriendship(Integer userId, Integer friendId) {
        users.get(userId).getFriends().remove(friendId);
    }

    public boolean isUserExist(Integer id) {
        return users.containsKey(id);
    }

    public boolean isMailExist(String mail) {
        return users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(m -> m.equals(mail));
    }
}
