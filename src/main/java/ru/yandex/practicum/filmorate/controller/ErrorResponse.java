package ru.yandex.practicum.filmorate.controller;

public class ErrorResponse {
    String error;
    String description;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
}
