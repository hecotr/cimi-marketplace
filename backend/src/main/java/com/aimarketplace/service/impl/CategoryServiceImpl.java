package com.aimarketplace.service.impl;

import com.aimarketplace.common.BusinessException;
import com.aimarketplace.dto.CategoryDTO;
import com.aimarketplace.entity.Category;
import com.aimarketplace.mapper.CategoryMapper;
import com.aimarketplace.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getCategoriesByType(String assetType) {
        List<Category> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>()
                .eq(Category::getAssetType, assetType)
                .orderByAsc(Category::getSortOrder)
        );

        return categories.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getAssetType)
                .orderByAsc(Category::getSortOrder)
        );

        return categories.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return toDTO(category);
    }

    @Override
    public Long createCategory(Category category) {
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateCategory(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }
        category.setId(id);
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.deleteById(id);
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}
