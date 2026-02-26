package com.aimarketplace.service;

import com.aimarketplace.dto.CategoryDTO;
import com.aimarketplace.entity.Category;

import java.util.List;

public interface CategoryService {

    /**
     * 根据资产类型获取分类列表
     */
    List<CategoryDTO> getCategoriesByType(String assetType);

    /**
     * 获取所有分类
     */
    List<CategoryDTO> getAllCategories();

    /**
     * 获取分类详情
     */
    CategoryDTO getCategoryById(Long id);

    /**
     * 创建分类（管理员）
     */
    Long createCategory(Category category);

    /**
     * 更新分类（管理员）
     */
    void updateCategory(Long id, Category category);

    /**
     * 删除分类（管理员）
     */
    void deleteCategory(Long id);
}
