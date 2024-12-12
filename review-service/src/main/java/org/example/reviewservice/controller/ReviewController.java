package org.example.reviewservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.reviewservice.dto.ReviewRequestDto;
import org.example.reviewservice.entity.Review;
import org.example.reviewservice.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(){
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Review>> getReviewsByBookId(@PathVariable Long bookId){
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId));
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        return ResponseEntity.ok(reviewService.saveReview(reviewRequestDto));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}