package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    Integer id;
    String login;
    String name;
    String email;
    LocalDate birthday;
    Set<Integer> friends = new HashSet<>();
    Set<Integer> likes = new HashSet<>();
}
