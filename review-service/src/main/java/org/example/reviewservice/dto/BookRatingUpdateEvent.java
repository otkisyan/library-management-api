package org.example.reviewservice.dto;

public record BookRatingUpdateEvent(Long bookId, double averageRating) {
}
