package com.sayel.E_Commerce.controller;

import com.razorpay.RazorpayException;
import com.sayel.E_Commerce.dto.PaymentResponse;
import com.sayel.E_Commerce.dto.PaymentVerifyRequest;
import com.sayel.E_Commerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create/{orderId}")
    public PaymentResponse createPayment(@PathVariable Long orderId) throws RazorpayException {
        return paymentService.createPaymentOrder(orderId);
    }
    @PostMapping("/verify/{orderId}")
    public String paymentSuccess(@PathVariable Long orderId, @RequestBody PaymentVerifyRequest request) throws RazorpayException {
        return  paymentService.verifyPayment(orderId,request);
    }

    @GetMapping("/key")
    public java.util.Map<String, String> getPublicKey() {
        return java.util.Map.of("key", paymentService.getPublicKey());
    }
}
