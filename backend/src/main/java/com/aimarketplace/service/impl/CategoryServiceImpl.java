package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.CategoryDTO;
import com.aimarketplace.dto.CategoryRequest;
import com.aimarketplace.entity.Asset;
import com.aimarketplace.entity.Category;
import com.aimarketplace.mapper.AssetMapper;
import com.aimarketplace.mapper.CategoryMapper;
import com.aimarketplace.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Category Service Implementation
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private AssetMapper assetMapper;

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus(request.getStatus());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        save(category);
        return entityToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryRequest request) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder());
        category.setStatus(request.getStatus());
        category.setUpdatedAt(LocalDateTime.now());
        updateById(category);
        return entityToDTO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // Check if category has assets
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", id);
        long count = assetMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("Cannot delete category with assets");
        }

        // Check if category has children
        QueryWrapper<Category> childWrapper = new QueryWrapper<>();
        childWrapper.eq("parent_id", id);
        long childCount = count(childWrapper);
        if (childCount > 0) {
            throw new RuntimeException("Cannot delete category with subcategories");
        }

        removeById(id);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = getById(id);
        return category != null ? entityToDTO(category) : null;
    }

    @Override
    public List<CategoryDTO> getActiveCategories() {
        List<Category> categories = categoryMapper.findActive();
        return categories.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getCategoryTree() {
        List<Category> allCategories = list();
        Map<Long, List<Category>> groupedByParent = allCategories.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() != null ? c.getParentId() : 0L));

        List<CategoryDTO> rootCategories = new ArrayList<>();
        if (groupedByParent.containsKey(0L)) {
            for (Category category : groupedByParent.get(0L)) {
                CategoryDTO dto = entityToDTO(category);
                dto.setChildren(buildChildren(category.getId(), groupedByParent));
                rootCategories.add(dto);
            }
        }

        return rootCategories;
    }

    @Override
    public List<CategoryDTO> getCategoriesByStatus(String status) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        wrapper.orderByAsc("sort_order");
        return list(wrapper).stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSortOrder(Long id, Integer sortOrder) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }
        category.setSortOrder(sortOrder);
        category.setUpdatedAt(LocalDateTime.now());
        updateById(category);
    }

    private List<CategoryDTO> buildChildren(Long parentId, Map<Long, List<Category>> groupedByParent) {
        List<CategoryDTO> children = new ArrayList<>();
        if (groupedByParent.containsKey(parentId)) {
            for (Category category : groupedByParent.get(parentId)) {
                CategoryDTO dto = entityToDTO(category);
                dto.setChildren(buildChildren(category.getId(), groupedByParent));
                children.add(dto);
            }
        }
        return children;
    }

    private CategoryDTO entityToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);

        // Load parent name
        if (category.getParentId() != null) {
            Category parent = getById(category.getParentId());
            if (parent != null) {
                dto.setParentName(parent.getName());
            }
        }

        // Count assets in category
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", category.getId());
        wrapper.eq("status", "published");
        dto.setAssetCount((int) assetMapper.selectCount(wrapper));

        return dto;
    }
}
