package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAll() {
        log.info("запущен метод findAll в GenreService");
        return genreStorage.findAll();
    }

    public Genre findGenreById(Integer id) {
        log.info("запущен метод findGenreById (id = {}) в GenreService", id);
        return genreStorage.findById(id).orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден"));
    }
}
