package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.CategoryDTO;
import com.aimarketplace.dto.CategoryRequest;
import com.aimarketplace.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Admin Category Controller
 */
@RestController
@RequestMapping("/api/admin/category")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<CategoryDTO> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryDTO category = categoryService.createCategory(request);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    public Result<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        CategoryDTO category = categoryService.updateCategory(id, request);
        return Result.success(category);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CategoryDTO> getCategory(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    @GetMapping
    public Result<List<CategoryDTO>> getAllCategories(@RequestParam(required = false) String status) {
        List<CategoryDTO> categories;
        if (status != null && !status.isEmpty()) {
            categories = categoryService.getCategoriesByStatus(status);
        } else {
            categories = categoryService.getActiveCategories();
        }
        return Result.success(categories);
    }

    @GetMapping("/tree")
    public Result<List<CategoryDTO>> getCategoryTree() {
        List<CategoryDTO> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    @PutMapping("/{id}/sort")
    public Result<Void> updateSortOrder(@PathVariable Long id, @RequestParam Integer sortOrder) {
        categoryService.updateSortOrder(id, sortOrder);
        return Result.success();
    }
}
