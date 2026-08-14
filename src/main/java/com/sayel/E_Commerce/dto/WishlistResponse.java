package com.sayel.E_Commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishlistResponse {
    private Long productId;
    private String productName;
    private String imageUrl;
    private double price;
    private Double discountPrice;
    private int stock;
}
