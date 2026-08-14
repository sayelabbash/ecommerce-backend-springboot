package com.sayel.E_Commerce.repository;

import com.sayel.E_Commerce.entity.Product;
import com.sayel.E_Commerce.entity.Review;
import com.sayel.E_Commerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    Optional<Review> findByUserAndProduct(User user, Product product);
    void deleteByProductId(Long productId);
}
