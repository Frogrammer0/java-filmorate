package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * User.
 */
@Data
public class User {

    String login;
    String name;
    String email;
    LocalDate birthday;
    long id;



}
