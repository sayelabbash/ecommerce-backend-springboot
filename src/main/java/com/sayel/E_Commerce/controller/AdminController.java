package com.sayel.E_Commerce.controller;

import com.sayel.E_Commerce.dto.AdminStatsResponse;
import com.sayel.E_Commerce.entity.Order;
import com.sayel.E_Commerce.repository.CategoryRepository;
import com.sayel.E_Commerce.repository.OrderRepository;
import com.sayel.E_Commerce.repository.ProductRepository;
import com.sayel.E_Commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/stats")
    public AdminStatsResponse getStats() {
        double revenue = orderRepository.findAll().stream()
                .filter(o -> !"PENDING".equals(o.getStatus()) && !"CANCELLED".equals(o.getStatus()))
                .mapToDouble(Order::getTotalPrice)
                .sum();

        long pending = orderRepository.findAll().stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .count();

        return new AdminStatsResponse(
                productRepository.count(),
                orderRepository.count(),
                userRepository.count(),
                categoryRepository.count(),
                revenue,
                pending
        );
    }
}
