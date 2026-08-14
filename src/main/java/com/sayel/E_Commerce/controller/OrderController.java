package com.sayel.E_Commerce.controller;

import com.sayel.E_Commerce.dto.OrderRequest;
import com.sayel.E_Commerce.dto.OrderResponse;
import com.sayel.E_Commerce.dto.OrderStatusRequest;
import com.sayel.E_Commerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Long placeOrder(@RequestBody(required = false) OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping("/all")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/my")
    public List<OrderResponse> getMyOrders() {
        return orderService.getOrdersByUser();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @RequestBody @Valid OrderStatusRequest request) {
        return orderService.updateOrderStatus(id, request.getStatus());
    }
}
