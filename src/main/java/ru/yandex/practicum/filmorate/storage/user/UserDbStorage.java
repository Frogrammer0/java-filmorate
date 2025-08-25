package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("db")
public class UserDbStorage extends BaseStorage<User> implements UserStorage {

    private static final String FIND_BASE = """
             SELECT u.id,
                   u.login,
                   u.name,
                   u.email,
                   u.birthday,
                   COALESCE(f.friends, '') AS friends,
                   COALESCE(l.likes, '')   AS likes
            FROM users u
            LEFT JOIN (
                SELECT user_id, GROUP_CONCAT(friend_id) AS friends
                FROM friendship
                GROUP BY user_id
            ) f ON u.id = f.user_id
            LEFT JOIN (
                SELECT user_id, GROUP_CONCAT(film_id) AS likes
                FROM film_likes
                GROUP BY user_id
            ) l ON u.id = l.user_id
            """;
    private static final String FIND_ALL_QUERY = FIND_BASE + ";";
    private static final String FIND_BY_ID_QUERY = FIND_BASE + " WHERE u.id = ?;";
    private static final String INSERT_QUERY = "INSERT INTO users(login, name, email, birthday) "
            + "VALUES(?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String FIND_FRIENDS_BY_USER = """
            WITH base AS (
            """ + FIND_BASE + """
            )
            SELECT b.*
            FROM base b
            JOIN friendship fr ON b.id = fr.friend_id
            WHERE fr.user_id = ?;
            """;
    private static final String FIND_COMMON_FRIENDS = """
            WITH base AS (
            """ + FIND_BASE + """
                )
            SELECT b.*
            FROM base b
            JOIN (
                SELECT f1.friend_id
                FROM friendship f1
                JOIN friendship f2 ON f1.friend_id = f2.friend_id
                WHERE f1.user_id = ? AND f2.user_id = ?
            ) common ON b.id = common.friend_id;
            """;


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public User create(User user) {
        int id = insert(
                INSERT_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        log.info("метод update БД");
        update(UPDATE_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        log.info("обновили в БД user = {}", user);
        return Optional.of(user);
    }

    @Override
    public void addFriendship(Integer userId, Integer friendId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!isUserExist(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        update(sql, userId, friendId);
        log.info("Добавили дружбу между {} и {} в БД", userId, friendId);
    }


    @Override
    public void removeFriendship(Integer userId, Integer friendId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!isUserExist(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        log.info("Удалили дружбу между {} и {} из БД", userId, friendId);
        delete(sql, userId, friendId);
    }


    @Override
    public boolean isMailExist(String mail) {
        int count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, mail);
        return count > 0;
    }

    @Override
    public boolean isUserExist(Integer id) {
        int count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Integer.class, id);
        return count > 0;
    }

    @Override
    public List<User> findFriendsByUser(Integer id) {
        return findMany(FIND_FRIENDS_BY_USER, id);
    }

    @Override
    public List<User> showCommonFriends(Integer userId, Integer friendId) {
        return findMany(FIND_COMMON_FRIENDS, userId, friendId);
    }
}
