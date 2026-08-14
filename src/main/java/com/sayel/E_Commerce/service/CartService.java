package com.sayel.E_Commerce.service;

import com.sayel.E_Commerce.dto.CartResponse;
import com.sayel.E_Commerce.entity.CartItem;
import com.sayel.E_Commerce.entity.Product;
import com.sayel.E_Commerce.entity.User;
import com.sayel.E_Commerce.exception.BadRequestException;
import com.sayel.E_Commerce.exception.InsufficientStockException;
import com.sayel.E_Commerce.exception.ResourceNotFoundException;
import com.sayel.E_Commerce.repository.CartItemRepository;
import com.sayel.E_Commerce.repository.ProductRepository;
import com.sayel.E_Commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public String addToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existing = cartItemRepository.findByUserAndProduct(user, product);

        int desiredQuantity = quantity + existing.map(CartItem::getQuantity).orElse(0);
        if (desiredQuantity > product.getStock()) {
            throw new InsufficientStockException("Only " + product.getStock() + " unit(s) of \"" + product.getName() + "\" available");
        }

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(desiredQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return "Product added to cart";
    }

    public List<CartResponse> getCart() {
        User user = getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        return cartItems.stream().map(item -> new CartResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getImageUrl(),
                item.getQuantity(),
                item.getProduct().getPrice(),
                item.getProduct().getDiscountPrice(),
                item.getProduct().getStock()
        )).toList();
    }

    public String updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));

        if (quantity > product.getStock()) {
            throw new InsufficientStockException("Only " + product.getStock() + " unit(s) available");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return "Quantity updated";
    }

    public String removeItem(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));

        cartItemRepository.delete(item);
        return "Item removed from cart";
    }

    private CartItem getCartItem(Long productId) {
        User user = getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));
    }

    public String increaseQuantity(Long productId) {
        CartItem item = getCartItem(productId);
        if (item.getQuantity() + 1 > item.getProduct().getStock()) {
            throw new InsufficientStockException("Only " + item.getProduct().getStock() + " unit(s) available");
        }
        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(item);
        return "Quantity increased";
    }

    public String decreaseQuantity(Long productId) {
        CartItem item = getCartItem(productId);
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartItemRepository.save(item);
        } else {
            cartItemRepository.delete(item);
        }
        return "Quantity decreased";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
