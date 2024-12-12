package org.example.reviewservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.reviewservice.config.OAuth2TokenService;
import org.example.reviewservice.dto.BookRatingUpdateEvent;
import org.example.reviewservice.dto.ReviewRequestDto;
import org.example.reviewservice.entity.Review;
import org.example.reviewservice.repository.ReviewRepository;
import org.example.reviewservice.service.producer.ProducerService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final ProducerService producerService;
    private final OAuth2TokenService oauth2TokenService;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(IllegalArgumentException::new);
    }

    public List<Review> getReviewsByBookId(Long bookId) {
        checkBookExisting(bookId);
        return reviewRepository.findByBookId(bookId);
    }

    public Review saveReview(ReviewRequestDto reviewRequestDto) {
        checkBookExisting(reviewRequestDto.bookId());
        Review review = Review.builder()
                .content(reviewRequestDto.content())
                .rating(reviewRequestDto.rating())
                .bookId(reviewRequestDto.bookId()).build();

        Review savedReview = reviewRepository.save(review);
        double newAverageRating = calculateAverageRating(review.getBookId());
        sendRatingUpdateEvent(review.getBookId(), newAverageRating);

        return savedReview;
    }

    private void checkBookExisting(Long bookId) {
        Boolean bookExists = webClient.get()
                .uri("http://book-service/books/{bookId}/exists", bookId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if (!Boolean.TRUE.equals(bookExists)) {
            throw new IllegalArgumentException("Book does not exist");
        }
    }

//    private void checkBookExisting(Long bookId) {
//        String accessToken = oauth2TokenService.getAccessToken();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<Boolean> response =
//                restTemplate.exchange("http://book-service/books/{bookId}/exists",
//                        HttpMethod.GET,
//                        entity,
//                        Boolean.class,
//                        bookId);
//
//        if (!Boolean.TRUE.equals(response.getBody())) {
//            throw new IllegalArgumentException("Book does not exist");
//        }
//    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));
        Long bookId = review.getBookId();
        reviewRepository.delete(review);

        double newAverageRating = calculateAverageRating(bookId);
        sendRatingUpdateEvent(bookId, newAverageRating);
    }

    private double calculateAverageRating(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        return reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
    }

    private void sendRatingUpdateEvent(Long bookId, double newAverageRating) {
        BookRatingUpdateEvent event = new BookRatingUpdateEvent(bookId, newAverageRating);
        producerService.send("book.rating.update", event);
    }
}
