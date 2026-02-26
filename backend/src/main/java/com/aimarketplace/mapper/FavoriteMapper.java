package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
