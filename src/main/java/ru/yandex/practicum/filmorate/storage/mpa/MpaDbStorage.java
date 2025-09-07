package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("db")
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {


    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Mpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> findById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void validateMpa(Integer id) {
        log.info("запущен метод validateMpa в MpaStorage");
        boolean flag = findAll().stream()
                .map(Mpa::getId)
                .noneMatch(i -> Objects.equals(i, id));
        if (flag) {
            throw new NotFoundException("Id запрашиваемого рейтинга MPA ( id = " + id +
                    " ) не входит в список рейтингов");
        }
    }
}
