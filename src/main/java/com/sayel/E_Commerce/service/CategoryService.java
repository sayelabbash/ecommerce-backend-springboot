package com.sayel.E_Commerce.service;

import com.sayel.E_Commerce.dto.CategoryRequest;
import com.sayel.E_Commerce.dto.CategoryResponse;
import com.sayel.E_Commerce.entity.Category;
import com.sayel.E_Commerce.exception.BadRequestException;
import com.sayel.E_Commerce.exception.ResourceNotFoundException;
import com.sayel.E_Commerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public String deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        if (category.getProducts() != null &&
                !category.getProducts().isEmpty()) {

            throw new BadRequestException(
                    "Cannot delete a category that still has products");
        }

        categoryRepository.delete(category);

        return "Category deleted successfully";
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl(),
                category.getProducts() != null
                        ? category.getProducts().size()
                        : 0
        );
    }
}
