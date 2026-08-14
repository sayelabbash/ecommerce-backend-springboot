package com.sayel.E_Commerce.controller;

import com.sayel.E_Commerce.dto.CartResponse;
import com.sayel.E_Commerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        return cartService.addToCart(productId, quantity);
    }

    @GetMapping
    public List<CartResponse> getCart() {
        return cartService.getCart();
    }

    @PutMapping
    public String updateQuantity(@RequestParam Long productId, @RequestParam int quantity) {
        return cartService.updateQuantity(productId, quantity);
    }

    @DeleteMapping
    public String removeItem(@RequestParam Long productId) {
        return cartService.removeItem(productId);
    }

    @PostMapping("/increase")
    public String increaseQuantity(@RequestParam Long productId) {
        return cartService.increaseQuantity(productId);
    }

    @PostMapping("/decrease")
    public String decreaseQuantity(@RequestParam Long productId) {
        return cartService.decreaseQuantity(productId);
    }
}
