package com.aimarketplace.service;

import com.aimarketplace.dto.CategoryDTO;
import com.aimarketplace.dto.CategoryRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Category Service Interface
 */
public interface CategoryService extends IService<com.aimarketplace.entity.Category> {

    /**
     * Create a category
     */
    CategoryDTO createCategory(CategoryRequest request);

    /**
     * Update a category
     */
    CategoryDTO updateCategory(Long id, CategoryRequest request);

    /**
     * Delete a category
     */
    void deleteCategory(Long id);

    /**
     * Get category by ID
     */
    CategoryDTO getCategoryById(Long id);

    /**
     * Get all active categories
     */
    List<CategoryDTO> getActiveCategories();

    /**
     * Get category tree
     */
    List<CategoryDTO> getCategoryTree();

    /**
     * Get categories by type
     */
    List<CategoryDTO> getCategoriesByStatus(String status);

    /**
     * Update category sort order
     */
    void updateSortOrder(Long id, Integer sortOrder);
}
