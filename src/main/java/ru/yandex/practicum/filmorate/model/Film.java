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

    @NotBlank
    @Size(min = 1, max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    @Min(1)
    private Long duration;

}
