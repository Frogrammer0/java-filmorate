package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }


    @GetMapping
    public List<Mpa> findAll() {
        log.info("запущен метод findAll в MpaController");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Mpa findMpaById(@PathVariable int id) {
        log.info("запущен метод findMpaById (id = {}) в MpaController", id);
        return mpaService.findMpaById(id);
    }
}
