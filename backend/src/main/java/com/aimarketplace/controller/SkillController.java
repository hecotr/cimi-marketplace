package com.aimarketplace.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aimarketplace.common.Result;
import com.aimarketplace.dto.SkillDTO;
import com.aimarketplace.dto.SkillQueryRequest;
import com.aimarketplace.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public Result<IPage<SkillDTO>> querySkills(SkillQueryRequest request) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        return Result.success(skillService.querySkills(userId, request));
    }

    @GetMapping("/{id}")
    public Result<SkillDTO> getSkillDetail(@PathVariable Long id) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        return Result.success(skillService.getSkillDetail(userId, id));
    }

    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        return Result.success(skillService.getCategories());
    }

    @PostMapping("/{id}/like")
    public Result<Void> toggleLike(@PathVariable Long id) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        skillService.toggleLike(userId, id);
        return Result.success();
    }

    @PostMapping("/{id}/favorite")
    public Result<Void> toggleFavorite(@PathVariable Long id) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        skillService.toggleFavorite(userId, id);
        return Result.success();
    }

    @GetMapping("/favorites")
    public Result<IPage<SkillDTO>> getFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        return Result.success(skillService.getFavorites(userId, page, size));
    }

    @GetMapping("/{id}/view")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        skillService.incrementViewCount(id);
        return Result.success();
    }

    @GetMapping("/{id}/download")
    public Result<Void> incrementDownloadCount(@PathVariable Long id) {
        skillService.incrementDownloadCount(id);
        return Result.success();
    }
}
