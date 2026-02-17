package com.example.demo.repository;

import com.example.demo.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // 🔹 Fetch cart items with product entity
    @Query("""
        SELECT ci
        FROM CartItem ci
        JOIN FETCH ci.product
        WHERE ci.user.userId = :userId
    """)
    List<CartItem> findByUserId(Integer userId);

    // 🔹 Clear cart after successful order
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM CartItem ci
        WHERE ci.user.userId = :userId
    """)
    void deleteByUserId(Integer userId);
}
