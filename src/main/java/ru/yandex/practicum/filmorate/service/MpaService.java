package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(@Qualifier("db") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> findAll() {
        log.info("запущен метод findAll в MpaService");
        return mpaStorage.findAll();
    }

    public Mpa findMpaById(Integer id) {
        log.info("запущен метод findMpaById (id = {}) в MpaService", id);
        return mpaStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Рейтинг mpa c id = " + id + " не найден")
        );
    }


}
