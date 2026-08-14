package com.sayel.E_Commerce.controller;

import com.sayel.E_Commerce.dto.ReviewRequest;
import com.sayel.E_Commerce.dto.ReviewResponse;
import com.sayel.E_Commerce.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/api/products/{productId}/reviews")
    public List<ReviewResponse> getReviews(@PathVariable Long productId) {
        return reviewService.getReviewsForProduct(productId);
    }

    @PostMapping("/api/products/{productId}/reviews")
    public ReviewResponse addReview(@PathVariable Long productId, @RequestBody @Valid ReviewRequest request) {
        return reviewService.addOrUpdateReview(productId, request);
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return "Review deleted successfully";
    }
}
