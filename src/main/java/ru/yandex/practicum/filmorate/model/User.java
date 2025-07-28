package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {

    String login;
    String name;
    String email;
    LocalDate birthday;
    Long id;
    Set<Long> friends;
    Set<Long> likes;

}
