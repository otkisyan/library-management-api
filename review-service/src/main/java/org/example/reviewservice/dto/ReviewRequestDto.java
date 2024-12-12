package org.example.reviewservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDto(@NotNull @NotBlank String content,
                               @Max(5) @Min(1) int rating,
                               @NotNull Long bookId) {
}
