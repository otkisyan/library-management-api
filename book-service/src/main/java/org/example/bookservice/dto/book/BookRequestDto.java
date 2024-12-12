package org.example.bookservice.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookRequestDto(@NotNull @NotBlank String title,
                             @NotNull Long authorId,
                             @NotNull @NotEmpty List<Long> genreIds) {
}
