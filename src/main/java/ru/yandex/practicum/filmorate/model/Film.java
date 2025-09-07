package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    Integer id;
    String name;
    String description;
    Integer duration;
    LocalDate releaseDate;
    Set<Integer> likes = new HashSet<>();
    List<Genre> genres = new ArrayList<>();
    Mpa mpa;
}
