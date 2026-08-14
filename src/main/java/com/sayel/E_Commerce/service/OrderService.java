package com.sayel.E_Commerce.service;

import com.sayel.E_Commerce.dto.OrderItemResponse;
import com.sayel.E_Commerce.dto.OrderRequest;
import com.sayel.E_Commerce.dto.OrderResponse;
import com.sayel.E_Commerce.entity.CartItem;
import com.sayel.E_Commerce.entity.Order;
import com.sayel.E_Commerce.entity.OrderItem;
import com.sayel.E_Commerce.entity.Product;
import com.sayel.E_Commerce.entity.User;
import com.sayel.E_Commerce.exception.BadRequestException;
import com.sayel.E_Commerce.exception.InsufficientStockException;
import com.sayel.E_Commerce.exception.ResourceNotFoundException;
import com.sayel.E_Commerce.repository.CartItemRepository;
import com.sayel.E_Commerce.repository.OrderItemRepository;
import com.sayel.E_Commerce.repository.OrderRepository;
import com.sayel.E_Commerce.repository.ProductRepository;
import com.sayel.E_Commerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmailService emailService;

    private static final List<String> VALID_STATUSES = List.of("PENDING", "CONFIRMED", "PAID", "SHIPPED", "DELIVERED", "CANCELLED");

    @Transactional
    public Long placeOrder(OrderRequest request) {
        User user = getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // Validate stock for every item before touching anything.
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getStock()) {
                throw new InsufficientStockException(
                        "Only " + product.getStock() + " unit(s) of \"" + product.getName() + "\" left in stock");
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        if (request != null) {
            order.setShippingAddress(request.getShippingAddress());
            order.setPhone(request.getPhone());
            order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "RAZORPAY");
        } else {
            order.setPaymentMethod("RAZORPAY");
        }

        double total = 0;
        Order savedOrder = orderRepository.save(order);

        for (CartItem item : cartItems) {
            Product product = item.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice());

            total += item.getQuantity() * orderItem.getPrice();
            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        savedOrder.setTotalPrice(total);
        if ("COD".equalsIgnoreCase(savedOrder.getPaymentMethod())) {
            savedOrder.setStatus("CONFIRMED");
        }
        orderRepository.save(savedOrder);

        cartItemRepository.deleteAll(cartItems);

        if ("COD".equalsIgnoreCase(savedOrder.getPaymentMethod())) {
            try {
                emailService.sendEmail(
                        user.getEmail(),
                        "Order Confirmed",
                        "Your Cash on Delivery order #" + savedOrder.getId() + " has been placed. Total: " + total
                );
            } catch (Exception ignored) {
            }
        }

        return savedOrder.getId();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<OrderResponse> getOrdersByUser() {
        User user = getCurrentUser();
        return orderRepository.findByUser(user).stream().map(this::toResponse).toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        boolean isOwner = order.getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You do not have access to this order");
        }
        return toResponse(order);
    }

    @Transactional
    public String cancelOrder(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        boolean isOwner = order.getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You do not have access to this order");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new BadRequestException("Only pending orders can be cancelled");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return "Order cancelled successfully";
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        if (status == null || !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new BadRequestException("Invalid status. Must be one of " + VALID_STATUSES);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status.toUpperCase());
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemList = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            itemList.add(new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getProduct().getImageUrl(),
                    item.getQuantity(),
                    item.getPrice()
            ));
        }

        return new OrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getPhone(),
                order.getPaymentMethod(),
                order.getCreatedAt(),
                itemList
        );
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
