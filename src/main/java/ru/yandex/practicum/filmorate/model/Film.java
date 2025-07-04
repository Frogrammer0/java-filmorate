package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {


    long id;
    String name;
    String description;
    long duration;
    LocalDate releaseDate;
}
