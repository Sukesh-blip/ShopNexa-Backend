package com.example.demo.service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ==============================
    // GET CART ITEMS (FIXED)
    // ==============================
    @Transactional(readOnly = true)
    public Map<String, Object> getCartItems(Integer userId) {

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        List<Map<String, Object>> products = new ArrayList<>();
        BigDecimal overallTotal = BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            Product p = item.getProduct();

            BigDecimal pricePerUnit = p.getPrice();
            BigDecimal totalPrice = pricePerUnit.multiply(BigDecimal.valueOf(item.getQuantity()));

            Map<String, Object> productMap = new HashMap<>();
            productMap.put("product_id", p.getProductId());
            productMap.put("name", p.getName());
            productMap.put("description", p.getDescription());
            productMap.put("quantity", item.getQuantity());
            productMap.put("price_per_unit", pricePerUnit);
            productMap.put("total_price", totalPrice);
            productMap.put(
                    "image_url",
                    p.getImages() != null && !p.getImages().isEmpty()
                            ? p.getImages().get(0).getImageUrl()
                            : null);

            products.add(productMap);
            overallTotal = overallTotal.add(totalPrice);
        }

        Map<String, Object> cart = new HashMap<>();
        cart.put("products", products);
        cart.put("overall_total_price", overallTotal);

        return Map.of("cart", cart);
    }

    // ==============================
    // CART COUNT
    // ==============================
    @Transactional(readOnly = true)
    public int getCartItemCount(Integer userId) {
        return cartItemRepository.findByUserId(userId)
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // ==============================
    // ADD TO CART
    // ==============================
    @Transactional
    public void addToCart(Integer userId, Integer productId, Integer quantity) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<CartItem> existingItems = cartItemRepository.findByUserId(userId);

        for (CartItem item : existingItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
                return;
            }
        }

        CartItem newItem = new CartItem(user, product, quantity);
        cartItemRepository.save(newItem);
    }

    // ==============================
    // UPDATE QUANTITY
    // ==============================
    @Transactional
    public void updateCartItemQuantity(Integer userId, Integer productId, Integer quantity) {

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
                return;
            }
        }

        throw new RuntimeException("Cart item not found");
    }

    // ==============================
    // DELETE ITEM
    // ==============================
    @Transactional
    public void deleteCartItem(Integer userId, Integer productId) {

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                cartItemRepository.delete(item);
                return;
            }
        }
    }
}
