package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ✅ Get cart items
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(cartService.getCartItems(user.getUserId()));
    }

    // ✅ Cart item count
    @GetMapping("/items/count")
    public ResponseEntity<Integer> getCartItemCount(HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(cartService.getCartItemCount(user.getUserId()));
    }

    // ✅ ADD TO CART (FIXED)
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!body.containsKey("productId")) {
            return ResponseEntity.badRequest().body("productId is required");
        }

        int productId = ((Number) body.get("productId")).intValue();
        int quantity = body.containsKey("quantity")
                ? ((Number) body.get("quantity")).intValue()
                : 1;

        cartService.addToCart(user.getUserId(), productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ✅ UPDATE QUANTITY
    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!body.containsKey("productId") || !body.containsKey("quantity")) {
            return ResponseEntity.badRequest().body("productId and quantity required");
        }

        int productId = ((Number) body.get("productId")).intValue();
        int quantity = ((Number) body.get("quantity")).intValue();

        cartService.updateCartItemQuantity(user.getUserId(), productId, quantity);
        return ResponseEntity.ok().build();
    }

    // ✅ DELETE ITEM
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCartItem(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!body.containsKey("productId")) {
            return ResponseEntity.badRequest().body("productId required");
        }

        int productId = ((Number) body.get("productId")).intValue();
        cartService.deleteCartItem(user.getUserId(), productId);
        return ResponseEntity.noContent().build();
    }
}
