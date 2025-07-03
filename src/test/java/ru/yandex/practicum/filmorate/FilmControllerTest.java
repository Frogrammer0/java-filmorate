package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    @Test
    void shouldCreateFilmWithValidData() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testfilm");
        film.setDuration(120);
        film.setReleaseDate("2000-01-01");

        Film createdFilm = controller.create(film);

        assertEquals(1, createdFilm.getId());
        assertEquals("testFilm", createdFilm.getName());
    }

    @Test
    void shouldUpdateFilmWithValidData() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testfilm");
        film.setDuration(120);
        film.setReleaseDate("2000-01-01");

        Film createdFilm = controller.create(film);

        Film newFilm = new Film();
        newFilm.setName("updateFilm");
        newFilm.setDescription("updatetestfilm");
        newFilm.setDuration(100);
        newFilm.setReleaseDate("2000-01-01");
        newFilm.setId(1);


        Film updateFilm = controller.update(newFilm);

        assertEquals(1, updateFilm.getId());
        assertEquals("updateFilm", updateFilm.getName());
    }

    @Test
    void createFilmShouldFailWithNegativeDuration() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testfilm");
        film.setDuration(-120);
        film.setReleaseDate("2000-01-01");

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Длительность фильма не может быть отрицательной", ex.getMessage());
    }

    @Test
    void createUserShouldFailWithEmptyName() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("testfilm");
        film.setDuration(120);
        film.setReleaseDate("2000-01-01");

        ValidationException ex = assertThrows(ValidationException.class, () -> controller.create(film));
        assertEquals("Название фильма должно быть указано", ex.getMessage());
    }

    @Test
    void createUserShouldFailWithDuplicateEmail() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testfilm");
        film.setDuration(120);
        film.setReleaseDate("2000-01-01");

        Film createdFilm = controller.create(film);

        Film dFilm = new Film();
        dFilm.setName("testFilm");
        dFilm.setDescription("newtestfilm");
        dFilm.setDuration(122);
        dFilm.setReleaseDate("2000-01-01");


        DuplicatedDataException ex = assertThrows(DuplicatedDataException.class, () -> controller.create(dFilm));
        assertEquals("Этот фильм уже добавлен", ex.getMessage());
    }

    @Test
    void updateFilmShouldThrowIfFilmNotFound() {
        Film film = new Film();
        film.setName("testFilm");
        film.setDescription("testfilm");
        film.setDuration(122);
        film.setReleaseDate("2000-01-01");
        film.setId(99);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> controller.update(film));
        assertTrue(ex.getMessage().contains("Фильм с id = " + film.getId() + " не найден"));
    }
}
