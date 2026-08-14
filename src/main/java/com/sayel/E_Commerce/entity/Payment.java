package com.sayel.E_Commerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String status;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
