package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
