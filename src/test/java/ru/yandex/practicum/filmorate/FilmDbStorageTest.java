package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private UserDbStorage userDbStorage;
    @Autowired
    private FilmDbStorage filmDbStorage;

    @BeforeEach
    void setUp() {
        RowMapper<Film> mapper = (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setDuration(rs.getInt("duration"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            return film;
        };

        filmDbStorage = new FilmDbStorage(jdbc, mapper);
    }

    private Film createTestFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Описание для " + name);
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(List.of(new Genre(1, "Комедия")));
        return film;
    }

    @Test
    void testCreateAndFindFilm() {
        Film film = createTestFilm("Фильм 1");
        Film saved = filmDbStorage.create(film);

        assertThat(saved.getId()).isNotNull();

        Optional<Film> fromDb = filmDbStorage.findFilmById(saved.getId());
        assertThat(fromDb)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Фильм 1");
                    assertThat(f.getMpa().getId()).isEqualTo(1);
                });
    }

    @Test
    void testUpdateFilm() {
        Film film = createTestFilm("Фильм 2");
        Film saved = filmDbStorage.create(film);

        saved.setName("Обновленный фильм");
        saved.setDescription("Новое описание");
        Optional<Film> updated = filmDbStorage.update(saved);

        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Обновленный фильм");
                    assertThat(f.getDescription()).isEqualTo("Новое описание");
                });
    }


    @Test
    void testAddLike() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = userDbStorage.create(user);


        Film film = createTestFilm("Фильм 3");
        Film savedFilm = filmDbStorage.create(film);

        filmDbStorage.addLike(savedFilm.getId(), savedUser.getId());

        List<Film> topFilms = filmDbStorage.getTopFilm(10);
        assertThat(topFilms).isNotEmpty();
        assertThat(topFilms.get(0).getId()).isEqualTo(savedFilm.getId());
    }


    @Test
    void testFindAll() {
        Film film1 = filmDbStorage.create(createTestFilm("Фильм A"));
        Film film2 = filmDbStorage.create(createTestFilm("Фильм B"));

        List<Film> films = filmDbStorage.findAll();
        assertThat(films)
                .extracting(Film::getName)
                .contains("Фильм A", "Фильм B");
    }

    @Test
    void testIsFilmExist() {
        Film film = filmDbStorage.create(createTestFilm("Фильм С"));
        boolean existsById = filmDbStorage.isFilmExist(film.getId());
        boolean existsByName = filmDbStorage.isFilmExist("Фильм С");

        assertThat(existsById).isTrue();
        assertThat(existsByName).isTrue();
    }
}
