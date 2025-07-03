package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.Instant;

/**
 * User.
 */
@Data
public class User {

    String login;
    String name;
    String email;
    String birthday;
    long id;

    Instant instantBirthday;



}
