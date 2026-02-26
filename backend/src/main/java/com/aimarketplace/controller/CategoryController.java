package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.CategoryDTO;
import com.aimarketplace.entity.Category;
import com.aimarketplace.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据资产类型获取分类
     */
    @GetMapping
    public Result<List<CategoryDTO>> getCategories(
            @RequestParam(required = false) String assetType) {
        if (assetType != null) {
            return Result.success(categoryService.getCategoriesByType(assetType));
        }
        return Result.success(categoryService.getAllCategories());
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Result<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    /**
     * 创建分类（管理员）
     */
    @PostMapping("/admin")
    public Result<Long> createCategory(@RequestBody Category category) {
        return Result.success(categoryService.createCategory(category));
    }

    /**
     * 更新分类（管理员）
     */
    @PutMapping("/admin/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        categoryService.updateCategory(id, category);
        return Result.success();
    }

    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/admin/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
