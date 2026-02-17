package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("""
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.images
        WHERE (:categoryId IS NULL OR p.category.categoryId = :categoryId)
    """)
    List<Product> findProductsWithImages(Integer categoryId);

    @Query("""
        SELECT c.categoryName
        FROM Product p
        JOIN p.category c
        WHERE p.productId = :productId
    """)
    String findCategoryNameByProductId(Integer productId);
}
