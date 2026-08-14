package com.sayel.E_Commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {
    private Long productId;
    private String productName;
    private String imageUrl;
    private int quantity;
    private double price;
    private Double discountPrice;
    private int availableStock;
}
