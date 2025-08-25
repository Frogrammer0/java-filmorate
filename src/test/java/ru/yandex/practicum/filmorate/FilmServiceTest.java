package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, FilmService.class,
        UserDbStorage.class, UserRowMapper.class, UserService.class,
        MpaDbStorage.class, MpaRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class})
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    private Film film1;
    private Film film2;
    private User user1;

    @BeforeEach
    void setUp() {
        film1 = new Film();
        film1.setName("Film One");
        film1.setDescription("Description One");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new Mpa(1, "G"));

        film2 = new Film();
        film2.setName("Film Two");
        film2.setDescription("Description Two");
        film2.setReleaseDate(LocalDate.of(2005, 5, 5));
        film2.setDuration(90);
        film2.setMpa(new Mpa(2, "PG"));

        user1 = new User();
        user1.setLogin("login1");
        user1.setName("User One");
        user1.setEmail("one@mail.com");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userService.create(user1);
    }

    @Test
    void testCreateFilm() {
        Film created = filmService.create(film1);
        assertThat(created.getId()).isNotNull();
    }

    @Test
    void testCreateFilmWithDuplicateIdThrowsException() {
        Film created = filmService.create(film1);
        film2.setId(created.getId());


        assertThrows(DuplicatedDataException.class,
                () -> filmService.create(film2));
    }

    @Test
    void testFindAllFilms() {
        filmService.create(film1);
        filmService.create(film2);

        assertThat(filmService.findAll()).hasSize(2);
    }

    @Test
    void testFindFilmById() {
        Film created = filmService.create(film1);
        Film found = filmService.findFilmById(created.getId());
        assertThat(found.getName()).isEqualTo("Film One");
    }

    @Test
    void testFindFilmByIdThrowsNotFoundException() {
        assertThatThrownBy(() -> filmService.findFilmById(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testUpdateFilm() {
        Film created = filmService.create(film1);
        created.setName("Updated Film");
        Film updated = filmService.update(created);
        assertThat(updated.getName()).isEqualTo("Updated Film");
    }

    @Test
    void testUpdateFilmWithIdZeroThrowsValidationException() {
        film1.setId(0);
        assertThatThrownBy(() -> filmService.update(film1))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void testAddAndDeleteLike() {
        Film createdFilm = filmService.create(film1);
        Integer userId = user1.getId();
        Integer filmId = createdFilm.getId();

        filmService.addLike(userId, filmId);
        Film likedFilm = filmService.findFilmById(filmId);
        assertThat(likedFilm.getLikes()).contains(userId);

        filmService.deleteLike(userId, filmId);
        Film unlikedFilm = filmService.findFilmById(filmId);
        assertThat(unlikedFilm.getLikes()).doesNotContain(userId);
    }

    @Test
    void testGetTopFilmsValidation() {
        assertThrows(ValidationException.class,
                () -> filmService.getTopFilm(0));
    }

    @Test
    void testGetTopFilms() {
        filmService.create(film1);
        filmService.create(film2);

        List<Film> topFilms = filmService.getTopFilm(2);
        assertThat(topFilms).hasSize(2);
    }
}
