package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
        Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
        film.setMpa(mpa);
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        if (genre.getName() != null) {
            film.getGenres().add(genre);
        }
        String likesStr = rs.getString("likes");
        Set<Integer> likes = new HashSet<>();
        if (likesStr != null && !likesStr.isEmpty()) {
            for (String s : likesStr.split(",")) {
                likes.add(Integer.valueOf(s));
            }
        }
        film.setLikes(likes);

        log.info("В RowMapper сформирован фильм = {}", film);
        return film;
    }

}
