package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull(message = "Длительность фильма должна быть задана")
    @Positive
    private Long duration;

    // Скрыл, иначе падают тесты. Скорее всего в следующем спринте будем дорабатывать классы
//    @NotNull
//    private Integer ratingOfFilmByMpaId;
}
