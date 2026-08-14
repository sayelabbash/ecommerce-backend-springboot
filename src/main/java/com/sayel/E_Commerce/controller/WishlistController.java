package com.sayel.E_Commerce.controller;

import com.sayel.E_Commerce.dto.WishlistResponse;
import com.sayel.E_Commerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping
    public List<WishlistResponse> getWishlist() {
        return wishlistService.getWishlist();
    }

    @PostMapping("/{productId}")
    public String addToWishlist(@PathVariable Long productId) {
        return wishlistService.addToWishlist(productId);
    }

    @DeleteMapping("/{productId}")
    public String removeFromWishlist(@PathVariable Long productId) {
        return wishlistService.removeFromWishlist(productId);
    }
}
