package com.sayel.E_Commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {
    private long totalProducts;
    private long totalOrders;
    private long totalUsers;
    private long totalCategories;
    private double totalRevenue;
    private long pendingOrders;
}
