package com.sayel.E_Commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private double price;
    private Double discountPrice;
    private String description;
    private String brand;
    private String imageUrl;
    private List<String> images;
    private int stock;
    private double averageRating;
    private int numReviews;
    private Long categoryId;
    private String categoryName;
}
