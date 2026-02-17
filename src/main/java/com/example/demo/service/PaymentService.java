package com.example.demo.service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderStatus;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public PaymentService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createOrder(int userId, BigDecimal totalAmount) throws RazorpayException {

        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        com.razorpay.Order razorpayOrder = client.orders.create(orderRequest);

        // Fetch user to properly associate
        com.example.demo.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setOrderId(razorpayOrder.get("id"));
        order.setUser(user); // ✅ Set the object, not just ID
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now()); // Good practice to set this too

        orderRepository.save(order);
        return razorpayOrder.get("id");
    }

    @Transactional
    public boolean verifyPayment(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature,
            int userId) {

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            boolean valid = com.razorpay.Utils
                    .verifyPaymentSignature(attributes, razorpayKeySecret);

            if (!valid)
                return false;

            Order order = orderRepository.findById(razorpayOrderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setStatus(OrderStatus.APPROVED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            // ✅ Fetch cart items correctly
            List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProductId(cartItem.getProduct().getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPricePerUnit(cartItem.getProduct().getPrice());
                orderItem.setTotalPrice(
                        cartItem.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                orderItemRepository.save(orderItem);
            }

            // ✅ Clear cart
            cartItemRepository.deleteByUserId(userId);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
