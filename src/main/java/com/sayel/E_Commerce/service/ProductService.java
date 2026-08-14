package com.sayel.E_Commerce.service;

import com.sayel.E_Commerce.dto.PageResponse;
import com.sayel.E_Commerce.dto.ProductRequest;
import com.sayel.E_Commerce.dto.ProductResponse;
import com.sayel.E_Commerce.entity.Category;
import com.sayel.E_Commerce.entity.Product;
import com.sayel.E_Commerce.entity.Review;
import com.sayel.E_Commerce.exception.ResourceNotFoundException;
import com.sayel.E_Commerce.repository.CategoryRepository;
import com.sayel.E_Commerce.repository.ProductRepository;
import com.sayel.E_Commerce.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public ProductResponse createProduct(ProductRequest request){
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()->new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        applyRequest(product, request, category);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public PageResponse<ProductResponse> searchProducts(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            boolean inStock,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        Sort sort = buildSort(sortBy, direction);
        Pageable pageable = PageRequest.of(Math.max(page, 0), size <= 0 ? 12 : size, sort);

        Page<Product> result = productRepository.search(
                (keyword == null || keyword.isBlank()) ? null : keyword.trim(),
                categoryId,
                minPrice,
                maxPrice,
                inStock,
                pageable
        );

        List<ProductResponse> content = new ArrayList<>();
        for (Product p : result.getContent()) {
            content.add(toResponse(p));
        }

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    private Sort buildSort(String sortBy, String direction) {
        String field = switch (sortBy == null ? "" : sortBy) {
            case "price" -> "price";
            case "rating" -> "averageRating";
            case "newest" -> "createdAt";
            case "name" -> "name";
            default -> "id";
        };
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, field);
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = new ArrayList<>();
        for (Product product : products) {
            responses.add(toResponse(product));
        }
        return responses;
    }

    public ProductResponse updateProduct(Long id, ProductRequest request){
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()->new ResourceNotFoundException("Category not found"));

        applyRequest(product, request, category);

        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    private void applyRequest(Product product, ProductRequest request, Category category) {
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());
        product.setImages(request.getImages() != null ? request.getImages() : new ArrayList<>());
        product.setStock(request.getStock());
        product.setCategory(category);
    }

    public void deleteProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
        reviewRepository.deleteByProductId(id);
        productRepository.delete(product);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toResponse(product);
    }

    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    /** Recalculates and persists the average rating & review count for a product. */
    public void refreshRatingStats(Product product, ReviewRepository repo) {
        List<Review> reviews = repo.findByProductIdOrderByCreatedAtDesc(product.getId());
        if (reviews.isEmpty()) {
            product.setAverageRating(0);
            product.setNumReviews(0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
            product.setAverageRating(Math.round(avg * 10.0) / 10.0);
            product.setNumReviews(reviews.size());
        }
        productRepository.save(product);
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getDescription(),
                product.getBrand(),
                product.getImageUrl(),
                product.getImages(),
                product.getStock(),
                product.getAverageRating(),
                product.getNumReviews(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }
}
