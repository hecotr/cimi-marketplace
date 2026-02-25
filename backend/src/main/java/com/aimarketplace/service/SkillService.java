package com.aimarketplace.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aimarketplace.dto.SkillDTO;
import com.aimarketplace.dto.SkillQueryRequest;

import java.util.List;

public interface SkillService {
    IPage<SkillDTO> querySkills(Long userId, SkillQueryRequest request);
    SkillDTO getSkillDetail(Long userId, Long id);
    List<String> getCategories();
    void toggleLike(Long userId, Long id);
    void toggleFavorite(Long userId, Long id);
    IPage<SkillDTO> getFavorites(Long userId, Integer page, Integer size);
    void incrementViewCount(Long id);
    void incrementDownloadCount(Long id);
}
