package com.sayel.E_Commerce.service;

import com.sayel.E_Commerce.dto.WishlistResponse;
import com.sayel.E_Commerce.entity.Product;
import com.sayel.E_Commerce.entity.User;
import com.sayel.E_Commerce.entity.WishlistItem;
import com.sayel.E_Commerce.exception.ResourceNotFoundException;
import com.sayel.E_Commerce.repository.ProductRepository;
import com.sayel.E_Commerce.repository.UserRepository;
import com.sayel.E_Commerce.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public String addToWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (wishlistItemRepository.findByUserAndProduct(user, product).isPresent()) {
            return "Product already in wishlist";
        }

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);
        wishlistItemRepository.save(item);
        return "Product added to wishlist";
    }

    public List<WishlistResponse> getWishlist() {
        User user = getCurrentUser();
        return wishlistItemRepository.findByUser(user).stream()
                .map(item -> new WishlistResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getProduct().getPrice(),
                        item.getProduct().getDiscountPrice(),
                        item.getProduct().getStock()
                )).toList();
    }

    public String removeFromWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        wishlistItemRepository.deleteByUserAndProduct(user, product);
        return "Product removed from wishlist";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
