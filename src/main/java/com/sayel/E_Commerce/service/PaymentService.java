package com.sayel.E_Commerce.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.sayel.E_Commerce.dto.PaymentResponse;
import com.sayel.E_Commerce.dto.PaymentVerifyRequest;
import com.sayel.E_Commerce.entity.Order;
import com.sayel.E_Commerce.entity.Payment;
import com.sayel.E_Commerce.exception.BadRequestException;
import com.sayel.E_Commerce.exception.ResourceNotFoundException;
import com.sayel.E_Commerce.repository.OrderRepository;
import com.sayel.E_Commerce.repository.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    @Value("${razorpay.key}")
    private String key;
    @Value("${razorpay.secret}")
    private String secret;

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;

    public String getPublicKey() {
        return key;
    }

    public PaymentResponse createPaymentOrder(Long orderId) throws RazorpayException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResourceNotFoundException("Order not found"));
        if("PAID".equals(order.getStatus())){
            throw new BadRequestException("Order is already paid");
        }
        RazorpayClient client = new RazorpayClient(key,secret);

        long amountInPaise = Math.round(order.getTotalPrice() * 100);

        JSONObject object = new JSONObject();
        object.put("amount",amountInPaise);
        object.put("currency","INR");
        object.put("receipt","order_"+orderId);

        com.razorpay.Order razorpayOrder = client.orders.create(object);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setRazorpayOrderId(razorpayOrder.get("id"));
        payment.setStatus("CREATED");
        paymentRepository.save(payment);

        return new PaymentResponse(
                razorpayOrder.get("id"),
                razorpayOrder.get("amount")
        );
    }
    public String verifyPayment(Long orderId, PaymentVerifyRequest request) throws RazorpayException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResourceNotFoundException("Order not found"));
        if(order.getStatus().equals("PAID")) {
            throw new BadRequestException("Already paid");
        }
        String razorpayOrderId = request.getRazorpay_order_id();
        String paymentId = request.getRazorpay_payment_id();
        String signature = request.getRazorpay_signature();
        boolean isValid = paymentVerify(razorpayOrderId,paymentId,signature);

        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(()->new ResourceNotFoundException("Payment not found"));

        if(!isValid){
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            throw new BadRequestException("Invalid payment signature");
        }
        payment.setRazorpayPaymentId(paymentId);
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);
        order.setRazorpayPaymentId(paymentId);
        order.setStatus("PAID");
        orderRepository.save(order);
        try {
            emailService.sendEmail(
                    order.getUser().getEmail(),
                    "Order Confirmed",
                    "Your payment is successful. Order confirmed. Total: " + order.getTotalPrice()
            );
        }catch (Exception e){
            System.out.println("Email Failed");;
        }
        return "Payment successful and order confirmed. Payment ID : "+ paymentId;
    }
    public boolean paymentVerify(
            String razorpayOrderId,
            String paymentId,
            String signature) throws RazorpayException {

        String data = razorpayOrderId + "|" + paymentId;

        return Utils.verifySignature(data, signature, secret);
    }
}
