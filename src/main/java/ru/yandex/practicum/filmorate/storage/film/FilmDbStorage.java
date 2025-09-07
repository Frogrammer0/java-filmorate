package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.*;


@Slf4j
@Component
@Qualifier("db")
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private static final String BASE_SELECT_QUERY = """
             SELECT f.id,
                    f.name,
                    f.description,
                    f.duration,
                    f.release_date,
                    f.mpa_id,
                    m.name AS mpa_name,
                    g.id AS genre_id,
                    g.name AS genre_name,
                    COALESCE(l.likes, '')  AS likes,
                    COALESCE(l.like_count, 0) AS like_count
             FROM films f
             LEFT JOIN film_genres fg ON f.id = fg.film_id
             LEFT JOIN genre g ON fg.genre_id = g.id
             LEFT JOIN mpa m ON f.mpa_id = m.id
             LEFT JOIN (
             SELECT film_id,
                    GROUP_CONCAT(user_id) AS likes,
                    COUNT(user_id) AS like_count
             FROM film_likes
             GROUP BY film_id
             ) l ON f.id = l.film_id
            """;

    private static final String FIND_TOP_FILM_QUERY = BASE_SELECT_QUERY + """
            ORDER BY l.like_count DESC
            LIMIT ?
            """;

    private static final String FIND_ALL_QUERY = BASE_SELECT_QUERY + """
            """;
    private static final String FIND_BY_ID_QUERY = BASE_SELECT_QUERY + """
            WHERE f.id = ?
            """;

    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films(name, description, duration, mpa_id, release_date)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?, description = ?, duration = ?, mpa_id = ?, release_date = ?
            WHERE id = ?
            """;

    private static final String UPDATE_FILM_WITHOUT_MPA_QUERY = """
            UPDATE films
            SET name = ?, description = ?, duration = ?, release_date = ?
            WHERE id = ?
            """;

    private static final String INSERT_LIKE_QUERY = """
            MERGE INTO film_likes(user_id, film_id)
            KEY(user_id, film_id)
            VALUES (?, ?)
            """;

    private static final String REMOVE_LIKE_QUERY = """
            DELETE FROM film_likes
            WHERE film_id = ?
            AND user_id = ?;
            """;

    public Map<Integer, Film> films = new HashMap<>();


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        List<Film> filmList = findMany(FIND_BY_ID_QUERY, id);
        if (filmList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(buildFilm(filmList));
        }
    }

    @Override
    public Film create(Film film) {
        log.info("запущен метод create фильм в DB");
        Integer id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getReleaseDate()
        );
        film.setId(id);
        log.info("в БД создан фильм = {}", film);

        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (film.getMpa() != null) {
            update(
                    UPDATE_FILM_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getReleaseDate(),
                    film.getId()
            );
        } else {
            update(
                    UPDATE_FILM_WITHOUT_MPA_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getDuration(),
                    film.getReleaseDate(),
                    film.getId()
            );
        }
        log.info("обновили в БД film = {}", film);
        return findFilmById(film.getId());
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        update(INSERT_LIKE_QUERY, userId, filmId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {} в FilmDb", userId, filmId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        update(REMOVE_LIKE_QUERY, filmId, userId);
        log.info("Пользователь с id = {} удалил лайк у фильма с id = {} в FilmDb", userId, filmId);
    }

    @Override
    public List<Film> getTopFilm(long count) {
        return findMany(FIND_TOP_FILM_QUERY, count);
    }

    @Override
    public boolean isFilmExist(Integer id) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean isFilmExist(String name) {
        String sql = "SELECT COUNT(*) FROM films WHERE name = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    private Film buildFilm(List<Film> filmList) {
        Film film = filmList.get(0);

        Set<Genre> genres = new HashSet<>();
        for (Film f : filmList) {
            if (f.getGenres() != null) {
                genres.addAll(f.getGenres());
            }
        }
        film.setGenres(new ArrayList<>(genres));

        return film;
    }


}
