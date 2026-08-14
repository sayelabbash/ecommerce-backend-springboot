package com.sayel.E_Commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private double totalPrice;
    private String status;
    private String shippingAddress;
    private String phone;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
