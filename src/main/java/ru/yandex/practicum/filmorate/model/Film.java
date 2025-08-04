package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    Long id;
    String name;
    String description;
    Long duration;
    LocalDate releaseDate;
    Set<Long> likes = new HashSet<>();
    Set<Genre> genre;
    Rating rating;
}
