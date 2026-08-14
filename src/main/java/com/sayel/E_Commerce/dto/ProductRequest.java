package com.sayel.E_Commerce.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    @Positive(message = "Price must be greater than 0")
    private double price;

    private Double discountPrice;

    private String description;

    private String brand;

    private String imageUrl;

    private List<String> images;

    @PositiveOrZero(message = "Stock cannot be negative")
    private int stock;

    @NotNull(message = "Category is required")
    private Long categoryId;
}
