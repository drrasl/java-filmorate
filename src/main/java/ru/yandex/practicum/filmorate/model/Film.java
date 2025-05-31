package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @NotNull
    private Mpa mpa = new Mpa();

    private List<Genres> genres = new ArrayList<>();
}
