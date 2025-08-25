package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> findAll() {
        log.info("запущен метод findAll в GenreController");
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Genre findGenreById(@PathVariable int id) {
        log.info("запущен метод findGenreById (id = {}) в GenreController", id);
        return genreService.findGenreById(id);
    }
}
