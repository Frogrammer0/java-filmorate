package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Component
@Qualifier("db")
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    private static final Integer maxSize = 6;
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String GET_GENRES_SIZE = "SELECT COUNT(id) FROM genre";

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

    private void validateGenre(int id) {
        Integer count = jdbc.queryForObject(GET_GENRES_SIZE, Integer.class);
        if (count == null || id > maxSize) {
            throw new NotFoundException("Id запрашиваемого жанра ( id = " + id + " ) не входит в список жанров");
        }
    }
}
