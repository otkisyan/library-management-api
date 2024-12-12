package org.example.bookservice.dto.book;

public record BookRatingUpdateEvent(Long bookId, double averageRating) {

}
