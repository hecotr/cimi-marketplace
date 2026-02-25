package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.SkillDTO;
import com.aimarketplace.dto.SkillQueryRequest;
import com.aimarketplace.entity.Skill;
import com.aimarketplace.entity.SkillFavorite;
import com.aimarketplace.entity.SkillLike;
import com.aimarketplace.mapper.SkillFavoriteMapper;
import com.aimarketplace.mapper.SkillLikeMapper;
import com.aimarketplace.mapper.SkillMapper;
import com.aimarketplace.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skill> implements SkillService {

    private final SkillLikeMapper skillLikeMapper;
    private final SkillFavoriteMapper skillFavoriteMapper;

    @Override
    public IPage<SkillDTO> querySkills(Long userId, SkillQueryRequest request) {
        Page<Skill> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Skill::getName, request.getKeyword())
                    .or().like(Skill::getDescription, request.getKeyword()));
        }
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            wrapper.eq(Skill::getCategory, request.getCategory());
        }

        // Sorting
        if ("view_count".equals(request.getSortBy())) {
            wrapper.orderByDesc(Skill::getViewCount);
        } else if ("download_count".equals(request.getSortBy())) {
            wrapper.orderByDesc(Skill::getDownloadCount);
        } else if ("like_count".equals(request.getSortBy())) {
            wrapper.orderByDesc(Skill::getLikeCount);
        } else {
            wrapper.orderByDesc(Skill::getCreatedAt);
        }

        IPage<Skill> skillPage = page(page, wrapper);
        return skillPage.convert(skill -> {
            SkillDTO dto = toDTO(skill);
            if (userId != null) {
                dto.setIsLiked(checkIsLiked(userId, skill.getId()));
                dto.setIsFavorited(checkIsFavorited(userId, skill.getId()));
            }
            return dto;
        });
    }

    @Override
    public SkillDTO getSkillDetail(Long userId, Long id) {
        Skill skill = getById(id);
        SkillDTO dto = toDTO(skill);
        if (userId != null) {
            dto.setIsLiked(checkIsLiked(userId, id));
            dto.setIsFavorited(checkIsFavorited(userId, id));
        }
        return dto;
    }

    @Override
    public List<String> getCategories() {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Skill::getCategory);
        wrapper.isNotNull(Skill::getCategory);
        wrapper.groupBy(Skill::getCategory);
        return list(wrapper).stream()
                .map(Skill::getCategory)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleLike(Long userId, Long skillId) {
        LambdaQueryWrapper<SkillLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillLike::getUserId, userId).eq(SkillLike::getSkillId, skillId);
        SkillLike existing = skillLikeMapper.selectOne(wrapper);

        if (existing != null) {
            skillLikeMapper.deleteById(existing.getId());
            // Decrement like count
            Skill skill = getById(skillId);
            skill.setLikeCount(skill.getLikeCount() - 1);
            updateById(skill);
        } else {
            SkillLike like = new SkillLike();
            like.setUserId(userId);
            like.setSkillId(skillId);
            skillLikeMapper.insert(like);
            // Increment like count
            Skill skill = getById(skillId);
            skill.setLikeCount(skill.getLikeCount() + 1);
            updateById(skill);
        }
    }

    @Override
    public void toggleFavorite(Long userId, Long skillId) {
        LambdaQueryWrapper<SkillFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillFavorite::getUserId, userId).eq(SkillFavorite::getSkillId, skillId);
        SkillFavorite existing = skillFavoriteMapper.selectOne(wrapper);

        if (existing != null) {
            skillFavoriteMapper.deleteById(existing.getId());
        } else {
            SkillFavorite favorite = new SkillFavorite();
            favorite.setUserId(userId);
            favorite.setSkillId(skillId);
            skillFavoriteMapper.insert(favorite);
        }
    }

    @Override
    public IPage<SkillDTO> getFavorites(Long userId, Integer page, Integer size) {
        Page<Skill> skillPage = new Page<>(page, size);
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.inSql(Skill::getId, "SELECT skill_id FROM skill_favorite WHERE user_id = " + userId);
        wrapper.orderByDesc(Skill::getCreatedAt);
        IPage<Skill> resultPage = page(skillPage, wrapper);
        return resultPage.convert(this::toDTO);
    }

    @Override
    public void incrementViewCount(Long id) {
        Skill skill = getById(id);
        skill.setViewCount(skill.getViewCount() + 1);
        updateById(skill);
    }

    @Override
    public void incrementDownloadCount(Long id) {
        Skill skill = getById(id);
        skill.setDownloadCount(skill.getDownloadCount() + 1);
        updateById(skill);
    }

    private SkillDTO toDTO(Skill skill) {
        if (skill == null) return null;
        SkillDTO dto = new SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setDescription(skill.getDescription());
        dto.setCategory(skill.getCategory());
        dto.setType(skill.getType());
        dto.setViewCount(skill.getViewCount());
        dto.setDownloadCount(skill.getDownloadCount());
        dto.setLikeCount(skill.getLikeCount());
        return dto;
    }

    private boolean checkIsLiked(Long userId, Long skillId) {
        LambdaQueryWrapper<SkillLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillLike::getUserId, userId).eq(SkillLike::getSkillId, skillId);
        return skillLikeMapper.selectCount(wrapper) > 0;
    }

    private boolean checkIsFavorited(Long userId, Long skillId) {
        LambdaQueryWrapper<SkillFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillFavorite::getUserId, userId).eq(SkillFavorite::getSkillId, skillId);
        return skillFavoriteMapper.selectCount(wrapper) > 0;
    }
}
