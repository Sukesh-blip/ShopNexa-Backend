package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            String token = authService.generateToken(user);

            Cookie cookie = new Cookie("authToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // ✅ Set to true for Azure HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 1 hour
            // cookie.setDomain("localhost"); // ❌ Removed hardcoded domain
            response.addCookie(cookie);

            // ✅ Manual header for cross-domain support if needed
            response.addHeader("Set-Cookie",
                    String.format("authToken=%s; HttpOnly; Path=/; Max-Age=3600; Secure; SameSite=None", token));

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            responseBody.put("token", token); // ✅ EXPOSE TOKEN FOR SWAGGER/TESTING
            responseBody.put("role", user.getRole().name());
            responseBody.put("username", user.getUsername());
            responseBody.put("userId", user.getUserId());

            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        // 🔹 Delete auth cookie (always)
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // ✅ true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(0);
        // cookie.setDomain("localhost"); // ❌ Match login domain
        response.addCookie(cookie);

        // 🔹 Optional server-side cleanup
        User user = (User) request.getAttribute("authenticatedUser");
        if (user != null) {
            authService.logout(user); // invalidate DB token if you store tokens
        }

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logout successful");

        return ResponseEntity.ok(responseBody);
    }

}
