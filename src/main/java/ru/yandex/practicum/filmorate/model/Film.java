package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.Instant;

/**
 * Film.
 */
@Data
public class Film {

    final Instant birthdayFilm = Instant.parse("1895-12-28T00:00:01Z");
    long id;
    String name;
    String description;
    long duration;
    Instant instantReleaseDate;
    String releaseDate;
}
