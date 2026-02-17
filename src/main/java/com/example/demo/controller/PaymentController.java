package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.PaymentService;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ==============================
    // CREATE RAZORPAY ORDER
    // ==============================
    @PostMapping("/create")
    public ResponseEntity<String> createPaymentOrder(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        try {
            User user = (User) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            if (!requestBody.containsKey("totalAmount")) {
                return ResponseEntity.badRequest()
                        .body("totalAmount is required");
            }

            BigDecimal totalAmount = new BigDecimal(requestBody.get("totalAmount").toString());

            String razorpayOrderId = paymentService.createOrder(user.getUserId(), totalAmount);

            return ResponseEntity.ok(razorpayOrderId);

        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating Razorpay order");
        }
    }

    // ==============================
    // VERIFY PAYMENT
    // ==============================
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        try {
            User user = (User) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            String razorpayOrderId = (String) requestBody.get("razorpayOrderId");
            String razorpayPaymentId = (String) requestBody.get("razorpayPaymentId");
            String razorpaySignature = (String) requestBody.get("razorpaySignature");

            boolean verified = paymentService.verifyPayment(
                    razorpayOrderId,
                    razorpayPaymentId,
                    razorpaySignature,
                    user.getUserId());

            if (verified) {
                return ResponseEntity.ok("Payment verified successfully");
            } else {
                return ResponseEntity.badRequest()
                        .body("Payment verification failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying payment");
        }
    }
}
