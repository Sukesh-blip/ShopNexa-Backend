package com.example.demo.service;

import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ProductResponseDTO> getProducts(String category) {

        Integer categoryId = null;

        if (category != null && !category.isBlank()) {
            // First, try to parse as ID (numeric)
            try {
                categoryId = Integer.parseInt(category);
                // If parsing succeeds, verify it actually exists to avoid generic query failure
                if (!categoryRepository.existsById(categoryId)) {
                    throw new RuntimeException("Category ID " + categoryId + " not found");
                }
            } catch (NumberFormatException e) {
                // If not numeric, treat as name
                categoryId = categoryRepository.findByCategoryName(category)
                        .orElseThrow(() -> new RuntimeException("Category '" + category + "' not found"))
                        .getCategoryId();
            }
        }

        List<Product> products = productRepository.findProductsWithImages(categoryId);

        return products.stream().map(p -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setProductId(p.getProductId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setStock(p.getStock());
            dto.setImages(
                    p.getImages()
                            .stream()
                            .map(img -> img.getImageUrl())
                            .toList());
            return dto;
        }).toList();
    }
}
