package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("db")
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String INSERT_GENRE_QUERY = """
            MERGE INTO genre(name)
            KEY (name)
            VALUES (?)
            """;

    private static final String INSERT_FILM_GENRE_QUERY = """
            INSERT INTO film_genres(film_id, genre_id)
            VALUES (?, ?)
            """;
    private static final String DELETE_GENRES_QUERY = """
            DELETE FROM film_genres WHERE film_id = ?
            """;

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> findById(int id) {
        validateGenre(id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void validateGenre(int id) {
        boolean flag = findAll().stream()
                .map(Genre::getId)
                .noneMatch(i -> i == id);
        if (flag) {
            throw new NotFoundException("Id запрашиваемого жанра ( id = " + id + " ) не входит в список жанров");
        }
    }

    @Override
    public void saveGenre(Film film) {
        log.info("запущен метод saveGenres в DB");

        if (!film.getGenres().isEmpty()) {
            film.getGenres().forEach(g -> {
                        if (film.getGenres().stream()
                                .mapToInt(Genre::getId)
                                .max()
                                .orElse(0) > 20) {
                            throw new NotFoundException("добавляемый жанр не существует");
                        }
                        update(INSERT_GENRE_QUERY, g.getName());
                    }
            );
        }
    }

    @Override
    public void saveFilmGenres(Film film) {
        log.info("запущен метод saveFilmGenres в DB");
        if (!film.getGenres().isEmpty()) {
            film.getGenres().stream().distinct().forEach(genre -> {
                if (genre != null) {
                    update(INSERT_FILM_GENRE_QUERY, film.getId(), genre.getId());
                }
            });
        }
    }

    @Override
    public void updateGenres(Film film) {
        update(DELETE_GENRES_QUERY, film.getId());
        saveFilmGenres(film);
    }


}
