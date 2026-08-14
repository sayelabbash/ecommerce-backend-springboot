package com.sayel.E_Commerce.service;

import com.sayel.E_Commerce.dto.ReviewRequest;
import com.sayel.E_Commerce.dto.ReviewResponse;
import com.sayel.E_Commerce.entity.Product;
import com.sayel.E_Commerce.entity.Review;
import com.sayel.E_Commerce.entity.User;
import com.sayel.E_Commerce.exception.BadRequestException;
import com.sayel.E_Commerce.exception.ResourceNotFoundException;
import com.sayel.E_Commerce.repository.ProductRepository;
import com.sayel.E_Commerce.repository.ReviewRepository;
import com.sayel.E_Commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @Transactional
    public ReviewResponse addOrUpdateReview(Long productId, ReviewRequest request) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Review review = reviewRepository.findByUserAndProduct(user, product)
                .orElse(new Review());

        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);
        productService.refreshRatingStats(product, reviewRepository);

        return toResponse(saved);
    }

    public List<ReviewResponse> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        User user = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        boolean isOwner = review.getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You can only delete your own review");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);
        productService.refreshRatingStats(product, reviewRepository);
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
