package com.example.demo.adminservices;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product addProductWithImage(
            String name,
            String description,
            Double price,
            Integer stock,
            Integer categoryId,
            String imageUrl
    ) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        product.setStock(stock);
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Product image URL cannot be empty");
        }

        ProductImage image = new ProductImage();
        image.setImageUrl(imageUrl);

        // 🔥 THIS IS THE KEY LINE
        product.addImage(image);

        // 🔥 ONE SAVE ONLY (cascade handles image)
        return productRepository.save(product);
    }

    public void deleteProduct(Integer productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // 🔥 Cascade + orphanRemoval handles images
        productRepository.delete(product);
    }
}
