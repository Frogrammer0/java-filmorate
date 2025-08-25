package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));

        java.sql.Date birthday = rs.getDate("birthday");
        if (birthday != null) {
            user.setBirthday(birthday.toLocalDate());
        }


        String friendsStr = rs.getString("friends");
        Set<Integer> friends = new HashSet<>();
        if (friendsStr != null && !friendsStr.isBlank()) {
            friends = Arrays.stream(friendsStr.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }

        String likesStr = rs.getString("likes");
        Set<Integer> likes = new HashSet<>();
        if (likesStr != null && !likesStr.isBlank()) {
            likes = Arrays.stream(likesStr.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }

        user.setLikes(likes);
        user.setFriends(friends);

        return user;


    }
}
