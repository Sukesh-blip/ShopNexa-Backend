package com.example.demo.filter;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthenticationFilter(
            AuthService authService,
            UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        logger.info("✅ AuthenticationFilter initialized");
    }

    /**
     * ✅ STRICTLY SKIP PUBLIC ENDPOINTS
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/api/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/")
                || path.equals("/health")
                || path.equals("/status");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // ✅ Let OPTIONS pass cleanly
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        if (token == null || !authService.validateToken(token)) {
            // ❗ DO NOT BLOCK HERE — let Spring Security handle it
            filterChain.doFilter(request, response);
            return;
        }

        String username = authService.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ SET SECURITY CONTEXT — Required for Spring Security's .authenticated()
        // check
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.setAttribute("authenticatedUser", user);
        logger.info("✅ Authenticated user: {}, Role: {}", username, user.getRole());

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() == null)
            return null;

        for (Cookie cookie : request.getCookies()) {
            if ("authToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
